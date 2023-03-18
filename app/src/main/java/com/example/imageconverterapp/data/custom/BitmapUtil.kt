package com.example.imageconverterapp.data.custom

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

fun Bitmap.compress(
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
    quality: Int = 100
): Bitmap {
    val byteArray = this.compressToByteArray(format, quality)

    return BitmapFactory.decodeByteArray(
        byteArray, 0, byteArray.size
    )
}

fun Bitmap.compressToByteArray(
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
    quality: Int = 100
): ByteArray {
    val stream = ByteArrayOutputStream()

    this.compress(
        format,
        quality,
        stream
    )

    return stream.toByteArray()
}