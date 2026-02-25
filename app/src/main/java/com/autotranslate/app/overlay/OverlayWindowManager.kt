package com.autotranslate.app.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.WindowManager

class OverlayWindowManager(private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private var bubbleView: FloatingBubbleView? = null
    private var overlayView: TranslationOverlayView? = null
    private var bubbleParams: WindowManager.LayoutParams? = null

    fun showBubble(onTap: () -> Unit) {
        if (bubbleView != null) return

        val bubble = FloatingBubbleView(context, onTap)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = getScreenWidth() - (72 * context.resources.displayMetrics.density).toInt()
            y = (200 * context.resources.displayMetrics.density).toInt()
        }

        bubble.onMove = { dx, dy ->
            params.x += dx
            params.y += dy
            windowManager.updateViewLayout(bubble, params)
        }

        windowManager.addView(bubble, params)
        bubbleView = bubble
        bubbleParams = params
    }

    fun hideBubble() {
        bubbleView?.visibility = android.view.View.INVISIBLE
    }

    fun showBubbleAgain() {
        bubbleView?.visibility = android.view.View.VISIBLE
    }

    fun setBubbleState(state: FloatingBubbleView.BubbleState) {
        bubbleView?.setState(state)
    }

    fun showTranslationOverlay(results: List<com.autotranslate.app.data.TranslationResult>) {
        hideTranslationOverlay()

        val overlay = TranslationOverlayView(context) { hideTranslationOverlay() }
        overlay.translationResults = results

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
            PixelFormat.TRANSLUCENT
        )

        windowManager.addView(overlay, params)
        overlayView = overlay
    }

    fun hideTranslationOverlay() {
        overlayView?.let {
            try {
                windowManager.removeView(it)
            } catch (_: Exception) {
            }
        }
        overlayView = null
    }

    fun removeAll() {
        bubbleView?.let {
            try {
                windowManager.removeView(it)
            } catch (_: Exception) {
            }
        }
        bubbleView = null
        bubbleParams = null
        hideTranslationOverlay()
    }

    private fun getScreenWidth(): Int {
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(metrics)
        return metrics.widthPixels
    }
}
