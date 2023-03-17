package com.example.imageconverterapp.ui

import android.graphics.Bitmap
import android.net.Uri
import com.example.imageconverterapp.data.BitmapConverter

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
        get() = { uri ->
            try {
                uri?.let { viewState?.openFile(it) }
            } catch (e: Exception) {
                viewState?.showError(Throwable("Error in open file"))
            }
        }

    // Для сохранения изображения
    override val saveLauncherCallback: (Uri?) -> Unit
        get() = { uri ->
            try {
                uri?.let {
                    val byteArray = converter.convertToByteArray(bitmap = bitmap)
                    viewState?.saveFile(it, byteArray)
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

    override fun onConvertImageClick() {
        bitmap = converter.convertToBitmap(bitmap = bitmap)
        viewState?.showConvertedImage(bitmap)
        viewState?.saveBtnEnable(true)
    }

    override fun openFileSuccess(bitmap: Bitmap) {
        this.bitmap = bitmap
        viewState?.showOriginalImage(bitmap)
        viewState?.convertBtnEnable(true)
    }

    override fun saveFileSuccess() {
        viewState?.saveBtnEnable(false)
    }
}