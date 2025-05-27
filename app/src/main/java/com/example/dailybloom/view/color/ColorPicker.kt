package com.example.dailybloom.view.color

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.example.dailybloom.R

class ColorPicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val colorGradientView: ColorGradientView
    private val layoutColorContainer: LinearLayout
    private val selectedColorView: View
    private val colorValuesTextView: TextView

    private var onColorSelectedListener: ((Int) -> Unit)? = null
    private var selectedColor: Int = Color.BLUE

    init {
        LayoutInflater.from(context).inflate(R.layout.color_picker, this, true)

        colorGradientView = findViewById(R.id.colorGradientView)
        layoutColorContainer = findViewById(R.id.layoutColorContainer)
        selectedColorView = findViewById(R.id.selectedColorView)
        colorValuesTextView = findViewById(R.id.colorValuesTextView)

        post { setupColorPicker() }
    }

    fun setOnColorSelectedListener(listener: (Int) -> Unit) {
        onColorSelectedListener = listener
    }

    fun setSelectedColor(color: Int) {
        selectedColor = color
        selectedColorView.setBackgroundColor(color)
        updateColorDisplay(color)
    }

    private fun updateColorDisplay(color: Int) {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        val hsvText = "HSV: %.1f°, %.1f%%, %.1f%%".format(hsv[0], hsv[1]*100, hsv[2]*100)

        colorValuesTextView.text = "RGB: $red, $green, $blue\n$hsvText"
    }

    private fun setupColorPicker() {
        layoutColorContainer.removeAllViews()

        val squareSize = resources.getDimensionPixelSize(R.dimen.color_square_size)
        val squareMargin = (squareSize * 0.25).toInt()
        val squareCount = 16

        val gradientWidth = colorGradientView.width

        if (gradientWidth <= 0) {
            post { setupColorPicker() }
            return
        }

        val step = gradientWidth / (squareCount + 1)

        for (i in 0 until squareCount) {
            val colorView = View(context)
            val layoutParams = LinearLayout.LayoutParams(squareSize, squareSize)
            layoutParams.setMargins(squareMargin, squareMargin, squareMargin, squareMargin)
            colorView.layoutParams = layoutParams

            val position = (i + 1) * step

            val hue = (position.toFloat() / gradientWidth) * 360f
            val hsv = floatArrayOf(hue, 1f, 1f)
            val color = Color.HSVToColor(hsv)

            colorView.setBackgroundColor(color)

            colorView.setOnClickListener {
                selectedColor = color
                selectedColorView.setBackgroundColor(color)
                updateColorDisplay(color)
                onColorSelectedListener?.invoke(color)
            }

            layoutColorContainer.addView(colorView)
        }
    }
}