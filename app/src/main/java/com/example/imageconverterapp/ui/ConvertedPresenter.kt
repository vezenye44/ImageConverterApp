package com.example.imageconverterapp.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import androidx.annotation.MainThread
import com.example.imageconverterapp.data.BitmapConverter
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class ConvertedPresenter(
    // Конвертер Bitmap -> Bitmap(in PNG) or ByteArray
    private val converter: BitmapConverter
) : ConverterContract.Presenter {

    private var viewState: ConverterContract.View? = null
    private lateinit var bitmap: Bitmap

    override fun attach(viewState: ConverterContract.View) {
        this.viewState = viewState
        viewState.convertBtnEnable(false)
        viewState.saveBtnEnable(false)
    }

    override fun detach() {
        viewState = null
    }

    // Коллбеки для registerForActivityResult()
    // Для открытия изображения
    override val openLauncherCallback: (Uri?) -> Unit
        get() = { nullableUri ->
            try {
                nullableUri?.let { uri ->
                    Single.just(uri)
                        .observeOn(Schedulers.io())
                        .map {
                            return@map viewState?.openFile(it)!!
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                            onSuccess = {
                                openFileSuccess(it)
                            }
                        )
                }
            } catch (e: Exception) {
                viewState?.showError(Throwable("Error in open file"))
            }
        }

    // Для сохранения изображения
    override val saveLauncherCallback: (Uri?) -> Unit
        get() = { uri ->
            try {
                uri?.let {
                    converter.convertToByteArray(bitmap = bitmap)
                        .map { bytes ->
                            viewState?.saveFile(it, bytes)
                            return@map bytes
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                            onSuccess = {
                                saveFileSuccess()
                            }
                        )
                }
            } catch (e: Exception) {
                viewState?.showError(Throwable("Error in save file"))
            }
        }

    override fun onOpenImageClick() {
        viewState?.launchOpenFile()
    }

    override fun onSaveImageClick() {
        viewState?.launchSaveFile()
    }

    @SuppressLint("CheckResult")
    override fun onConvertImageClick() {
        converter.convertToBitmap(bitmap = bitmap)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { bitMap ->
                    bitmap = bitMap
                    viewState?.showConvertedImage(bitmap)
                    viewState?.saveBtnEnable(true)
                }
            )
    }

    @MainThread
    private fun openFileSuccess(bitmap: Bitmap) {
        this.bitmap = bitmap
        viewState?.showOriginalImage(bitmap)
        viewState?.convertBtnEnable(true)
    }

    @MainThread
    private fun saveFileSuccess() {
        viewState?.saveBtnEnable(false)
        viewState?.clearConvertedImage()
    }
}