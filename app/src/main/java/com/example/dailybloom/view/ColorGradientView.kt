package com.example.dailybloom.view

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
        // Create colors for the HSV spectrum
        generateHsvColorSpectrum()
    }

    private fun generateHsvColorSpectrum() {
        // Generate colors across the hue spectrum (0-360 degrees)
        val colorCount = 360
        colors = IntArray(colorCount)

        for (i in 0 until colorCount) {
            val hsv = floatArrayOf(i.toFloat(), 1f, 1f) // Full saturation and value
            colors[i] = Color.HSVToColor(hsv)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Create a horizontal linear gradient for the HSV spectrum
        val gradient = LinearGradient(
            0f, 0f, width.toFloat(), 0f,
            colors, null, Shader.TileMode.CLAMP
        )

        paint.shader = gradient

        // Draw the gradient background
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }
}