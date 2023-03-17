package com.example.imageconverterapp.data

import android.graphics.Bitmap

interface BitmapConverter {
    fun convertToByteArray(
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
        quality: Int = 100,
        bitmap: Bitmap
    ): ByteArray

    fun convertToBitmap(
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
        quality: Int = 100,
        bitmap: Bitmap
    ): Bitmap
}
