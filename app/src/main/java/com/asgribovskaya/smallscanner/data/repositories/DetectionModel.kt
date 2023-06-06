package com.asgribovskaya.smallscanner.data.repositories

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.RectF
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.nio.ByteBuffer

object DetectionModel {

    fun runObjectDetection(bitmap: Bitmap): List<RectF> {
        val image = TensorImage.fromBitmap(bitmap.copy(Bitmap.Config.ARGB_8888, true))
        val detectorOptions = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(DetectorSettings.maxResults)
            .setScoreThreshold(DetectorSettings.confidence)
            .build()
        val detector = ObjectDetector.createFromBufferAndOptions(
            DetectorSettings.modelBuffer!!,
            detectorOptions
        )
        return detector.detect(image).map { it.boundingBox }
    }

    fun setAssetManager(assets: AssetManager) {
        if (DetectorSettings.modelBuffer == null) {
            val byteArray = assets.open(DetectorSettings.modelName).readBytes()
            val directBuffer = ByteBuffer.allocateDirect(byteArray.size)
            DetectorSettings.modelBuffer = directBuffer.put(ByteBuffer.wrap(byteArray))
        }
    }
}