package com.asgribovskaya.smallscanner.data.repositories

import java.nio.ByteBuffer

object DetectorSettings {
    const val maxResults = 50
    const val confidence = 0.5f
    const val modelName = "sku-base-640-480-fp16.tflite"
    var modelBuffer: ByteBuffer? = null
}