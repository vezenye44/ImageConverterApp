package com.example.imageconverterapp.data

import android.graphics.Bitmap
import com.example.imageconverterapp.data.custom.compress
import com.example.imageconverterapp.data.custom.compressToByteArray

class BitmapConverterImpl : BitmapConverter {
    override fun convertToByteArray(
        format: Bitmap.CompressFormat,
        quality: Int,
        bitmap: Bitmap
    ): ByteArray {
        return bitmap.compressToByteArray(format, quality)
    }

    override fun convertToBitmap(
        format: Bitmap.CompressFormat,
        quality: Int,
        bitmap: Bitmap
    ): Bitmap {
        return bitmap.compress(format, quality)
    }
}