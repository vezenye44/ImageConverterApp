package com.example.imageconverterapp.data

import android.graphics.Bitmap
import com.example.imageconverterapp.data.custom.compress
import com.example.imageconverterapp.data.custom.compressToByteArray
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class BitmapConverterImpl : BitmapConverter {
    override fun convertToByteArray(
        format: Bitmap.CompressFormat,
        quality: Int,
        bitmap: Bitmap
    ): Single<ByteArray> {
        return Single.just(bitmap)
            .observeOn(Schedulers.computation())
            .map { bitMap ->
                return@map bitMap.compressToByteArray(format, quality)
            }
    }

    override fun convertToBitmap(
        format: Bitmap.CompressFormat,
        quality: Int,
        bitmap: Bitmap
    ): Single<Bitmap> {
        return Single.just(bitmap)
            .observeOn(Schedulers.computation())
            .map { bitMap ->
                return@map bitMap.compress(format, quality)
            }
    }
}