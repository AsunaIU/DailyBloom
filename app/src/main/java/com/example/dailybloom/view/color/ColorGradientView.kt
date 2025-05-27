package com.example.dailybloom.view.color

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View

class ColorGradientView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint()
    private var colors: IntArray = IntArray(0)

    init {
        generateHsvColorSpectrum()
    }

    private fun generateHsvColorSpectrum() {
        val colorCount = 360
        colors = IntArray(colorCount)

        for (i in 0 until colorCount) {
            val hsv = floatArrayOf(i.toFloat(), 1f, 1f)
            colors[i] = Color.HSVToColor(hsv)
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val gradient = LinearGradient(
            0f, 0f, width.toFloat(), 0f,
            colors, null, Shader.TileMode.CLAMP
        )

        paint.shader = gradient

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }
}