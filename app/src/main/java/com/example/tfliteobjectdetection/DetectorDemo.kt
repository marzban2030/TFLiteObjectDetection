package com.example.tfliteobjectdetection

import android.graphics.Bitmap
import android.graphics.RectF

interface DetectorDemo {
    val name: String
    fun detect(image: Bitmap, rotationDegrees: Int, callback: (SimpleDetection?) -> Unit)
    fun dispose()
}

data class SimpleDetection(val boundingBox: RectF, val label: String)

fun RectF.normalize(width: Int, height: Int): RectF {
    return RectF(left / width, top / height, right / width, bottom / height)
}

fun RectF.denormalize(width: Int, height: Int): RectF {
    return RectF(left * width, top * height, right * width, bottom * height)
}