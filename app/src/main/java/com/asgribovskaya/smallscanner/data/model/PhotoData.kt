package com.asgribovskaya.smallscanner.data.model

import android.graphics.Bitmap
import android.graphics.RectF

data class PhotoData(
    var bitmap: Bitmap? = null,
    var detections: List<RectF>? = null
)
