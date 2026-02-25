package com.autotranslate.app.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.autotranslate.app.R

@SuppressLint("ViewConstructor")
class FloatingBubbleView(
    context: Context,
    private val onTap: () -> Unit,
) : View(context) {

    enum class BubbleState { IDLE, PROCESSING, DONE }

    private val bubbleSize = (56 * resources.displayMetrics.density).toInt()
    private val iconSize = (24 * resources.displayMetrics.density).toInt()

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF00BFA6.toInt()
        style = Paint.Style.FILL
        setShadowLayer(8f * resources.displayMetrics.density, 0f, 4f, 0x40000000)
    }

    private val processingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFCA28.toInt()
        style = Paint.Style.FILL
        setShadowLayer(8f * resources.displayMetrics.density, 0f, 4f, 0x40000000)
    }

    private val icon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_translate)?.apply {
        setTint(0xFF0A0E14.toInt())
    }

    private var state = BubbleState.IDLE

    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var moved = false
    private val clickThreshold = 10 * resources.displayMetrics.density

    var onMove: ((Int, Int) -> Unit)? = null

    private var lastX = 0f
    private var lastY = 0f

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(bubbleSize, bubbleSize)
    }

    override fun onDraw(canvas: Canvas) {
        val cx = width / 2f
        val cy = height / 2f
        val radius = width / 2f - 4 * resources.displayMetrics.density

        val paint = if (state == BubbleState.PROCESSING) processingPaint else bgPaint
        canvas.drawCircle(cx, cy, radius, paint)

        icon?.let {
            val left = (width - iconSize) / 2
            val top = (height - iconSize) / 2
            it.setBounds(left, top, left + iconSize, top + iconSize)
            it.draw(canvas)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                lastX = event.rawX
                lastY = event.rawY
                moved = false
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX - initialTouchX
                val dy = event.rawY - initialTouchY
                if (Math.abs(dx) > clickThreshold || Math.abs(dy) > clickThreshold) {
                    moved = true
                }
                if (moved) {
                    val moveX = (event.rawX - lastX).toInt()
                    val moveY = (event.rawY - lastY).toInt()
                    onMove?.invoke(moveX, moveY)
                }
                lastX = event.rawX
                lastY = event.rawY
                return true
            }

            MotionEvent.ACTION_UP -> {
                if (!moved) {
                    onTap()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun setState(newState: BubbleState) {
        state = newState
        invalidate()
    }
}
