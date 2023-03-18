package com.example.imageconverterapp.ui

import android.graphics.Bitmap
import android.net.Uri

interface ConverterContract {
    interface View {
        // Запуск ActivityResultLauncher.launch()
        fun launchOpenFile()
        fun launchSaveFile()

        fun showOriginalImage(bitmap: Bitmap)
        fun showConvertedImage(bitmap: Bitmap)
        fun showError(throwable: Throwable)

        fun openFile(uri: Uri): Bitmap
        fun saveFile(uri: Uri, bitmapXByteArray: ByteArray)

        fun convertBtnEnable(isEnable: Boolean)
        fun saveBtnEnable(isEnable: Boolean)
        fun clearConvertedImage()

    }

    interface Presenter {
        fun attach(viewState: View)
        fun detach()

        // Коллбеки для registerForActivityResult()
        val openLauncherCallback: (Uri?) -> Unit
        val saveLauncherCallback: (Uri?) -> Unit

        fun onOpenImageClick()
        fun onSaveImageClick()
        fun onConvertImageClick()
    }
}