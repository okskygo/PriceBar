package rex.okskygo.pricebar.library

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import androidx.core.content.ContextCompat
import rex.okskygo.pricebar.R

class PriceBarGraph(private val context: Context,
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


        val maxCountDto = prices.maxBy { it.count } ?: return
        val maxCount = maxCountDto.count

        val stepWidth = (right - left) / (prices.size + 1)
        val stepHeight = (bottom - top) / maxCount.toFloat()

        val leftPath = Path()
        leftPath.moveTo(left, bottom)

        var lastLeftBreakX: Float? = null
        var lastLeftBreakY: Float? = null
        var lastLeftHeight = 0f
        for ((i, priceDto) in prices.withIndex()) {
            if (left + ((i + 1) * stepWidth) >= leftThumb.thumbX) {
                val percent = (stepWidth - (left + ((i + 1) * stepWidth) - leftThumb.thumbX)) / stepWidth
                if (percent > 0) {
                    lastLeftBreakX = leftThumb.thumbX
                    lastLeftBreakY = bottom - (lastLeftHeight + (stepHeight * priceDto.count - lastLeftHeight) * percent)
                    leftPath.lineTo(lastLeftBreakX, lastLeftBreakY)
                }
                break
            } else {
                lastLeftHeight = (stepHeight * priceDto.count)
                leftPath.lineTo(left + ((i + 1) * stepWidth), bottom - (stepHeight * priceDto.count))
            }
        }
        if (lastLeftBreakX != null && lastLeftBreakY != null) {
            leftPath.lineTo(lastLeftBreakX, bottom)
            leftPath.close()
        }

        val centerPath = Path()
        var lastCenterBreakX: Float? = null
        var lastCenterBreakY: Float? = null
        var lastCenterHeight = 0f

        if (lastLeftBreakX != null && lastLeftBreakY != null) {
            centerPath.moveTo(lastLeftBreakX, bottom)
            centerPath.lineTo(lastLeftBreakX, lastLeftBreakY)
        } else {
            centerPath.moveTo(left, bottom)
        }
        for ((i, priceDto) in prices.withIndex()) {
            if (left + ((i + 1) * stepWidth) >= rightThumb.thumbX) {
                val percent = (stepWidth - (left + ((i + 1) * stepWidth) - rightThumb.thumbX)) / stepWidth
                if (percent > 0) {
                    lastCenterBreakX = rightThumb.thumbX
                    lastCenterBreakY = bottom - (lastCenterHeight + (stepHeight * priceDto.count - lastCenterHeight) * percent)
                    centerPath.lineTo(lastCenterBreakX, lastCenterBreakY)
                }
                break
            }
            if (lastLeftBreakX != null && lastLeftBreakY != null) {
                if (left + ((i + 1) * stepWidth) > lastLeftBreakX) {
                    lastCenterHeight = (stepHeight * priceDto.count)
                    centerPath.lineTo(left + ((i + 1) * stepWidth), bottom - (stepHeight * priceDto.count))
                }
            } else {
                lastCenterHeight = (stepHeight * priceDto.count)
                centerPath.lineTo(left + ((i + 1) * stepWidth), bottom - (stepHeight * priceDto.count))
            }

        }
        if (lastCenterBreakX != null && lastCenterBreakY != null) {
            centerPath.lineTo(lastCenterBreakX, bottom)
            centerPath.close()
        } else {
            centerPath.lineTo(right, bottom)
            centerPath.close()
        }
        val rightPath = Path()
        if (lastCenterBreakX != null && lastCenterBreakY != null) {
            rightPath.moveTo(lastCenterBreakX, bottom)
            rightPath.lineTo(lastCenterBreakX, lastCenterBreakY)
            for ((i, priceDto) in prices.withIndex()) {
                if (left + ((i + 1) * stepWidth) > lastCenterBreakX) {
                    rightPath.lineTo(left + ((i + 1) * stepWidth), bottom - (stepHeight * priceDto.count))
                }
            }
            rightPath.lineTo(right, bottom)
            rightPath.close()
        }

        canvas.drawPath(leftPath, outsidePaint)
        canvas.drawPath(centerPath, insidePaint)
        canvas.drawPath(rightPath, outsidePaint)
    }

}