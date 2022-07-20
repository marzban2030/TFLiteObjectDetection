package com.example.tfliteobjectdetection.detectors

import android.graphics.Bitmap
import androidx.core.graphics.toRectF
import androidx.work.DirectExecutor
import com.example.tfliteobjectdetection.SimpleDetection
import com.example.tfliteobjectdetection.DetectorDemo
import com.example.tfliteobjectdetection.normalize
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions

class MlKitDetector : DetectorDemo {
    private val detector = ObjectDetection.getClient(
        ObjectDetectorOptions.Builder()
            .enableClassification()
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
            .setExecutor(DirectExecutor.INSTANCE)
            .build()
    )

    override val name: String
        get() = "ML Kit"

    override fun detect(image: Bitmap, rotationDegrees: Int, callback: (SimpleDetection?) -> Unit) {
        val inputImage = InputImage.fromBitmap(image, rotationDegrees)
        detector.process(inputImage).addOnCompleteListener { taskResult ->
            val detectedObject = taskResult.result.firstOrNull()
            if (!taskResult.isSuccessful || detectedObject == null) {
                callback(null)
            } else {
                val rotate90 = rotationDegrees == 90 || rotationDegrees == 270
                val width = if (rotate90) inputImage.height else inputImage.width
                val height = if (rotate90) inputImage.width else inputImage.height
                callback(
                    SimpleDetection(
                        detectedObject.boundingBox.toRectF().normalize(width, height),
                        detectedObject.labels.joinToString { it.text + ":" + it.confidence })
                )
            }
        }
    }

    override fun dispose() {
        detector.close()
    }
}