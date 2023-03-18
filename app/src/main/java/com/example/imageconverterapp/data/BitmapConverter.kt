package com.example.imageconverterapp.data

import android.graphics.Bitmap
import io.reactivex.rxjava3.core.Single

interface BitmapConverter {
    fun convertToByteArray(
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
        quality: Int = 100,
        bitmap: Bitmap
    ): Single<ByteArray>

    fun convertToBitmap(
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
        quality: Int = 100,
        bitmap: Bitmap
    ): Single<Bitmap>
}


