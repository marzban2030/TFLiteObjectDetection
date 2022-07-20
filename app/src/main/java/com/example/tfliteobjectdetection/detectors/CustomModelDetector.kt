package com.example.tfliteobjectdetection.detectors

import android.content.Context
import android.graphics.Bitmap
import com.example.tfliteobjectdetection.SimpleDetection
import com.example.tfliteobjectdetection.DetectorDemo
import com.example.tfliteobjectdetection.ml.Model
import com.example.tfliteobjectdetection.normalize
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op

class CustomModelDetector(context: Context) : DetectorDemo {
    private val detector = Model.newInstance(context)

    override val name: String
        get() = "Custom Model"

    override fun detect(image: Bitmap, rotationDegrees: Int, callback: (SimpleDetection?) -> Unit) {
        val tensorImage = Rot90Op(-rotationDegrees / 90)
            .apply(TensorImage.fromBitmap(image))
        val width = tensorImage.width
        val height = tensorImage.height
        val modelOutput = detector.process(tensorImage)
        val detection = modelOutput.detectionResultList.maxByOrNull { it.scoreAsFloat }
            ?.let {
                SimpleDetection(
                    it.locationAsRectF.normalize(width, height),
                    it.categoryAsString + ": " + "%.2f".format(it.scoreAsFloat)
                )
            }
        callback(detection)
    }

    override fun dispose() {
        detector.close()
    }
}