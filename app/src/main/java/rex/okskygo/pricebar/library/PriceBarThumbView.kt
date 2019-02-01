package rex.okskygo.pricebar.library

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import androidx.core.content.ContextCompat
import rex.okskygo.pricebar.R
import kotlin.math.abs
import kotlin.math.pow

class PriceBarThumbView constructor(context: Context) : View(context) {

    var thumbX: Float = 0.0f
    var thumbY: Float = 0.0f

    var thumbRadius: Float = 0f
        set(value) {
            borderPaint.strokeWidth = value * 0.1f
            field = value
        }

    private var thumbPressed = false

    private val borderPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorPrimary)
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val circlePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(thumbX, thumbY, thumbRadius * 0.95f, circlePaint)
        canvas.drawCircle(thumbX, thumbY, thumbRadius * 0.95f, borderPaint)
        super.onDraw(canvas)
    }

    fun isInTargetZone(x: Float, y: Float): Boolean {
        return abs(x - thumbX).pow(2) + abs(y - thumbY).pow(2) <= thumbRadius.pow(2)
    }

    fun press() {
        thumbPressed = true
    }

    fun release() {
        thumbPressed = false
    }

    override fun isPressed(): Boolean {
        return thumbPressed
    }
}