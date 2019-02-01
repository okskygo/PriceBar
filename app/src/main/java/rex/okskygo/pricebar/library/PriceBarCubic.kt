package rex.okskygo.pricebar.library

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import androidx.core.content.ContextCompat
import rex.okskygo.pricebar.R
import kotlin.math.min

class PriceBarCubic(private val context: Context,
                    val left: Float,
                    val right: Float,
                    val top: Float,
                    val bottom: Float,
                    val step: Int,
                    val maxPrice: Int?) {

    private val insidePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorPrimary)
        alpha = (255 * 0.5f).toInt()
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val outsidePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorOutside)
        alpha = (255 * 0.5f).toInt()
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    fun draw(canvas: Canvas,
             leftThumb: PriceBarThumbView,
             rightThumb: PriceBarThumbView,
             prices: List<PriceDto>) {
        if (step == 0 || prices.isEmpty()) {
            return
        }
        val maxPrice = maxPrice ?: (prices.maxBy { it.price }?.price ?: 0)
        val maxCountDto = prices.maxBy { it.count } ?: return
        val maxCount = maxCountDto.count

        val stepCount = maxPrice / step
        val stepWidth = (right - left) / stepCount
        val stepHeight = (bottom - top) / maxCount.toFloat()

        val backgroundPath = Path()
        backgroundPath.moveTo(left, bottom)
        var lastPoint = PointF(left, bottom)
        for (i in 0..stepCount) {
            val found = prices.find { it.price == i * step }
            if (i == 0) {
                val point = PointF(left, bottom - ((found?.count ?: 0) * stepHeight))
                backgroundPath.lineTo(point.x, point.y)
                lastPoint = point
            } else if (i == stepCount) {
                val point = PointF(right, bottom - ((found?.count ?: 0) * stepHeight))
                backgroundPath.quadTo((point.x + lastPoint.x) / 2,
                                      (point.y + lastPoint.y) / 2,
                                      point.x,
                                      point.y)
                if (point.y != bottom) {
                    backgroundPath.lineTo(right, bottom)
                }
            } else {
                val next = prices.find { it.price == (i + 1) * step }

                val x1 = lastPoint.x
                val x2 = left + (i * stepWidth)
                val x3 = left + ((i + 1) * stepWidth)

                val y1 = lastPoint.y
                val y2 = bottom - (found?.count ?: 0) * stepHeight
                val y3 = bottom - (next?.count ?: 0) * stepHeight

                val cx1a = x1 + (x2 - x1) / 3
                val cy1a = min(bottom, y1 + (y2 - y1) / 3)
                val cx1b = x2 - (x3 - x1) / 3
                val cy1b = min(bottom, y2 - (y3 - y1) / 3)
                backgroundPath.cubicTo(cx1a, cy1a, cx1b, cy1b, x2, y2)
                lastPoint = PointF(x2, y2)
            }
        }
        backgroundPath.close()
        canvas.drawPath(backgroundPath, outsidePaint)
        val bitmap = Bitmap.createBitmap(canvas.width, canvas.height, Bitmap.Config.ARGB_8888)
        val insideCanvas = Canvas(bitmap)
        insideCanvas.clipRect(Rect(leftThumb.thumbX.toInt(), 0, rightThumb.thumbX.toInt(), canvas.height))
        insideCanvas.drawPath(backgroundPath, insidePaint)
        canvas.drawBitmap(bitmap, 0f, 0f, Paint())
    }
}