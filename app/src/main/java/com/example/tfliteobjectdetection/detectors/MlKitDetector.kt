package com.example.tfliteobjectdetection.detectors

import android.graphics.Bitmap
import androidx.core.graphics.toRectF
import androidx.work.DirectExecutor
import com.example.tfliteobjectdetection.SimpleDetection
import com.example.tfliteobjectdetection.DetectorDemo
import com.example.tfliteobjectdetection.normalize
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions

abstract class MlKitDetectorBase : DetectorDemo {
    protected abstract val detector: ObjectDetector

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

class MlKitDetector : MlKitDetectorBase() {
    override val detector = ObjectDetection.getClient(
        ObjectDetectorOptions.Builder()
            .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
            .enableClassification()
            .setExecutor(DirectExecutor.INSTANCE)
            .build()
    )

    override val name get() = "ML Kit"
}

class MlKitDetectorCustom : MlKitDetectorBase() {
    private val modelName = "efficientnet4.tflite"
    override val detector = createObjectDetector(modelName)

    override val name get() = "ML Kit: $modelName"

    private fun createObjectDetector(modelName: String): ObjectDetector {
        val localModel = LocalModel.Builder()
            .setAssetFilePath(modelName)
            .build()
        val options = CustomObjectDetectorOptions.Builder(localModel)
            .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
            .enableClassification()
            .setClassificationConfidenceThreshold(0.5f)
            .setMaxPerObjectLabelCount(3)
            .setExecutor(DirectExecutor.INSTANCE)
            .build()
        return ObjectDetection.getClient(options)
    }
}