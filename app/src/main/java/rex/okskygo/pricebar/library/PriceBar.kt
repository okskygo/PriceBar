package rex.okskygo.pricebar.library

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


data class PriceRange(val left: Int, val right: Int)

class PriceBar @JvmOverloads constructor(context: Context,
                                         attrs: AttributeSet? = null,
                                         defStyle: Int = 0)
    : View(context, attrs, defStyle) {

    private val thumbRadius = 20f.toPx

    private lateinit var leftThumb: PriceBarThumbView
    private lateinit var rightThumb: PriceBarThumbView
    private lateinit var priceBarBaseline: PriceBarBaseline
    private lateinit var priceBarGraph: PriceBarGraph
    private lateinit var priceBarCubic: PriceBarCubic

    private var diffX: Int = 0
    private var diffY: Int = 0
    private var lastX: Float = 0.0f
    private var lastY: Float = 0.0f

    var step: Int = 0
    var thumbStep: Int = 0
    var maxPrice: Int? = null
    var prices = listOf<PriceDto>()
        set(value) {
            field = value.sortedBy { it.price }
                    .groupBy { roundedByStep(it.price) }
                    .map { (stepPrice, list) -> PriceDto(stepPrice, list.sumBy { it.count }) }
            invalidate()
        }
    var onChangeListener: ((PriceRange) -> Unit)? = null

    private fun roundedByStep(price: Int): Int = (price + step - 1) / step * step
    private fun roundedByThumbStep(price: Int): Int = (price + thumbStep - 1) / thumbStep * thumbStep

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val marginHorizontal = thumbRadius
        val marginVertical = height - (thumbRadius)

        leftThumb = PriceBarThumbView(context)
        leftThumb.thumbX = marginHorizontal
        leftThumb.thumbY = marginVertical
        leftThumb.thumbRadius = thumbRadius

        rightThumb = PriceBarThumbView(context)
        rightThumb.thumbX = w.toFloat() - marginHorizontal
        rightThumb.thumbY = marginVertical
        rightThumb.thumbRadius = thumbRadius

        priceBarBaseline = PriceBarBaseline(context, marginHorizontal, width - marginHorizontal, marginVertical)
        priceBarGraph = PriceBarGraph(context, marginHorizontal, width - marginHorizontal, 0f, marginVertical, step,
                                      maxPrice)
        priceBarCubic = PriceBarCubic(context, marginHorizontal, width - marginHorizontal, 0f, marginVertical, step,
                                      maxPrice)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        priceBarCubic.draw(canvas, leftThumb, rightThumb, prices)
        priceBarBaseline.draw(canvas, leftThumb, rightThumb)
        leftThumb.draw(canvas)
        rightThumb.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }
        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                diffX = 0
                diffY = 0

                lastX = event.x
                lastY = event.y

                onActionDown(event.x, event.y)
                return true
            }

            MotionEvent.ACTION_UP -> {
                this.parent.requestDisallowInterceptTouchEvent(false)
                onActionUp(event.x, event.y)
                return true
            }

            MotionEvent.ACTION_CANCEL -> {
                this.parent.requestDisallowInterceptTouchEvent(false)
                onActionUp(event.x, event.y)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                onActionMove(event.x)
                this.parent.requestDisallowInterceptTouchEvent(true)
                val curX = event.x
                val curY = event.y
                diffX += Math.abs(curX - lastX).toInt()
                diffY += Math.abs(curY - lastY).toInt()
                lastX = curX
                lastY = curY

                if (diffX < diffY) {
                    //vertical touch
                    parent.requestDisallowInterceptTouchEvent(false)
                    return false
                }

                return true
            }

            else -> return false
        }

    }

    private fun onActionMove(x: Float) {
        if (leftThumb.isPressed) {
            moveThumb(leftThumb, x)
        } else if (rightThumb.isPressed) {
            moveThumb(rightThumb, x)
        }

        if (leftThumb.thumbX > rightThumb.thumbX) {
            val temp = leftThumb
            leftThumb = rightThumb
            rightThumb = temp
        }
        val maxPrice = maxPrice
        val max = maxPrice ?: (prices.maxBy { it.price }?.price ?: 0)
        val leftPrice = ((leftThumb.thumbX - leftThumb.thumbRadius) / (priceBarBaseline.right - priceBarBaseline.left)) * max
        val rightPrice = ((rightThumb.thumbX - rightThumb.thumbRadius) / (priceBarBaseline.right - priceBarBaseline.left)) * max
        onChangeListener?.invoke(PriceRange(roundedByThumbStep(leftPrice.toInt()), roundedByThumbStep(rightPrice.toInt())))
    }

    private fun moveThumb(thumb: PriceBarThumbView, x: Float) {
        when {
            x < priceBarBaseline.left -> {
                thumb.thumbX = priceBarBaseline.left
            }
            x > priceBarBaseline.right -> {
                thumb.thumbX = priceBarBaseline.right
            }
            x >= priceBarBaseline.left && x <= priceBarBaseline.right -> {
                thumb.thumbX = x
            }
        }
        invalidate()
    }

    private fun onActionUp(x: Float, y: Float) {
        if (leftThumb.isPressed) {
            releaseThumb(leftThumb)
        } else if (rightThumb.isPressed) {
            releaseThumb(rightThumb)
        }
    }

    private fun onActionDown(x: Float, y: Float) {
        if (!rightThumb.isPressed && leftThumb.isInTargetZone(x, y)) {
            pressThumb(leftThumb)
        } else if (!leftThumb.isPressed && rightThumb.isInTargetZone(x, y)) {
            pressThumb(rightThumb)
        }
    }

    private fun releaseThumb(thumb: PriceBarThumbView) {
        thumb.release()
    }

    private fun pressThumb(thumb: PriceBarThumbView) {
        thumb.press()
    }
}