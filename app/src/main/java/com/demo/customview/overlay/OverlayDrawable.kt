package com.demo.customview.overlay

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.drawable.Drawable
import kotlin.math.min

/**
 * TODO gxd 用Drawable来画图形
 */
class OverlayDrawable : Drawable() {
    private val paint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = Color.MAGENTA
        }
    }

    override fun draw(canvas: Canvas) {
        if (bounds.isEmpty) {
            bounds = canvas.clipBounds
        }

        val radius = min(bounds.width(), bounds.height()) / 2
        canvas.drawCircle(bounds.centerX().toFloat(), bounds.centerY().toFloat(), radius.toFloat(), paint)
    }

    override fun setAlpha(alpha: Int) {
        TODO("Not yet implemented")
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        TODO("Not yet implemented")
    }

    override fun getOpacity(): Int {
        TODO("Not yet implemented")
    }
}