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

    private var view: ConverterContract.View? = null

    private lateinit var bitmap: Bitmap

    //view state:
    private val stateOfView = object {
        var bitmapOriginalImage: Bitmap? = null
        var bitmapConvertedImage: Bitmap? = null
        var isShowError = false
        var enableSaveBtn = false
        var enableConvertBtn = false
    }

    override fun attach(viewState: ConverterContract.View) {
        this.view = viewState

        initView()
    }

    private fun initView() {
        view?.convertBtnEnable(stateOfView.enableConvertBtn)
        view?.saveBtnEnable(stateOfView.enableSaveBtn)
        stateOfView.bitmapOriginalImage?.let {
            view?.showOriginalImage(it)
        }
        stateOfView.bitmapConvertedImage?.let {
            view?.showConvertedImage(it)
        }
    }

    override fun detach() {
        view = null
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
                            return@map view?.openFile(it)!!
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                            onSuccess = {
                                openFileSuccess(it)
                            }
                        )
                }
            } catch (e: Exception) {
                stateOfView.isShowError = true
                view?.showError(Throwable("Error in open file"))
            }
        }

    // Для сохранения изображения
    override val saveLauncherCallback: (Uri?) -> Unit
        get() = { uri ->
            try {
                uri?.let {
                    converter.convertToByteArray(bitmap = bitmap)
                        .map { bytes ->
                            view?.saveFile(it, bytes)
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
                stateOfView.isShowError = true
                view?.showError(Throwable("Error in save file"))
            }
        }

    override fun onOpenImageClick() {
        view?.launchOpenFile()
    }

    override fun onSaveImageClick() {
        view?.launchSaveFile()
    }

    @SuppressLint("CheckResult")
    override fun onConvertImageClick() {
        converter.convertToBitmap(bitmap = bitmap)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { bitMap ->
                    stateOfView.bitmapConvertedImage = bitMap
                    stateOfView.enableSaveBtn = true

                    bitmap = bitMap
                    view?.showConvertedImage(bitmap)
                    view?.saveBtnEnable(true)
                }
            )
    }

    @MainThread
    private fun openFileSuccess(bitmap: Bitmap) {
        stateOfView.bitmapOriginalImage = bitmap
        stateOfView.enableConvertBtn = true

        this.bitmap = bitmap
        view?.showOriginalImage(bitmap)
        view?.convertBtnEnable(true)
    }

    @MainThread
    private fun saveFileSuccess() {
        stateOfView.enableSaveBtn = false
        stateOfView.bitmapConvertedImage = null

        view?.saveBtnEnable(false)
        view?.clearConvertedImage()
    }
}