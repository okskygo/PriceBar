package rex.okskygo.pricebar.library

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.ContextCompat
import rex.okskygo.pricebar.R
import kotlin.math.max
import kotlin.math.min

class PriceBarBaseline(private val context: Context,
                       val left: Float,
                       val right: Float,
                       val barY: Float) {

    private val baselineWidth = 2f.toPx

    private val insidePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorPrimary)
        strokeWidth = baselineWidth
        isAntiAlias = true
    }

    private val outsidePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorOutside)
        strokeWidth = baselineWidth
        isAntiAlias = true
    }

    fun draw(canvas: Canvas, leftThumbView: PriceBarThumbView, rightThumbView: PriceBarThumbView) {

        val insideStart = min(leftThumbView.thumbX + leftThumbView.thumbRadius,
                              rightThumbView.thumbX + rightThumbView.thumbRadius)
        val insideEnd = max(leftThumbView.thumbX - leftThumbView.thumbRadius,
                            rightThumbView.thumbX - leftThumbView.thumbRadius)
        if (insideStart < insideEnd) {
            canvas.drawLine(insideStart, barY, insideEnd, barY, insidePaint)
        }

        val leftOutsideEnd = min(leftThumbView.thumbX - leftThumbView.thumbRadius,
                                 rightThumbView.thumbX - rightThumbView.thumbRadius)

        if (leftOutsideEnd > left) {
            canvas.drawLine(left, barY, leftOutsideEnd, barY, outsidePaint)
        }

        val rightOutsideStart = max(leftThumbView.thumbX + leftThumbView.thumbRadius,
                                    rightThumbView.thumbX + rightThumbView.thumbRadius)

        if (right > rightOutsideStart) {
            canvas.drawLine(rightOutsideStart, barY, right, barY, outsidePaint)
        }

    }
}