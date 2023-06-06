package com.asgribovskaya.smallscanner.ui.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.asgribovskaya.smallscanner.data.model.PhotoData
import kotlin.math.roundToInt

object PictureUtils {

    private const val destWidth: Int = 640
    private const val destHeight: Int = 480

    fun scaleBitmap(bitmap: Bitmap): Bitmap {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            inBitmap = bitmap
        }
        val srcWidth = options.outWidth.toFloat()
        val srcHeight = options.outHeight.toFloat()
        val sampleSize = if (srcHeight <= destHeight && srcWidth <= destWidth) 1
        else {
            val heightScale = srcHeight / destHeight
            val widthScale = srcWidth / destWidth
            minOf(heightScale, widthScale).roundToInt()
        }
        BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inBitmap = bitmap
        }
        return bitmap
    }

    fun drawBoundingBoxes(photoData: PhotoData, customColor: Int): Bitmap {
        val newBitmap = photoData.bitmap?.copy(Bitmap.Config.ARGB_8888, true)!!
        val canvas = Canvas(newBitmap)
        val paint = Paint().apply {
            color = customColor
        }
        val resizedDetections = resizeBoundingBoxes(photoData.detections!!)
        resizedDetections.forEach {
            canvas.drawRect(it, paint)
        }
        return newBitmap
    }

    private fun resizeBoundingBoxes(boxes: List<RectF>): List<RectF> = boxes.map {
        RectF(
            it.left * destWidth,
            it.top * destHeight,
            it.left * destWidth + (it.right - it.left),
            it.top * destHeight + (it.bottom - it.top)
        )
    }
}