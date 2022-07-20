package com.example.tfliteobjectdetection.detectors

import android.content.Context
import android.graphics.Bitmap
import com.example.tfliteobjectdetection.DetectorDemo
import com.example.tfliteobjectdetection.SimpleDetection
import com.example.tfliteobjectdetection.normalize
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.detector.ObjectDetector

class TasksLibraryDetector(context: Context) : DetectorDemo {
    private val modelName = "efficientdet3.tflite"
    private val detector = createObjectDetector(context, modelName)

    override val name: String
        get() = "Tasks Library: $modelName"

    private fun createObjectDetector(context: Context, modelName: String): ObjectDetector {
        val baseOptionsBuilder = BaseOptions.builder().setNumThreads(2)
        val optionsBuilder = ObjectDetector.ObjectDetectorOptions.builder()
            .setScoreThreshold(0.1f)
            .setMaxResults(1)
            .setBaseOptions(baseOptionsBuilder.build())

        return ObjectDetector.createFromFileAndOptions(context, modelName, optionsBuilder.build())
    }

    override fun detect(image: Bitmap, rotationDegrees: Int, callback: (SimpleDetection?) -> Unit) {
        val tensorImage = Rot90Op(-rotationDegrees / 90)
            .apply(TensorImage.fromBitmap(image))
        val width = tensorImage.width
        val height = tensorImage.height
        val modelOutput = detector.detect(tensorImage)
        val detection = modelOutput.firstOrNull()?.let {
            SimpleDetection(
                it.boundingBox.normalize(width, height),
                it.categories.joinToString { category ->
                    category.label + ": " + "%.2f".format(category.score)
                }
            )
        }
        callback(detection)
    }

    override fun dispose() = Unit
}