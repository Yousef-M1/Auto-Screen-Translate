package com.autotranslate.app.overlay

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.autotranslate.app.data.PrefsManager
import com.autotranslate.app.data.TranslationResult

@SuppressLint("ViewConstructor")
class TranslationOverlayView(
    context: Context,
    private val onDismiss: () -> Unit,
) : View(context) {

    private val density = resources.displayMetrics.density

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF1A2130.toInt()
        style = Paint.Style.FILL
    }

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFE8EDF4.toInt()
        textAlign = Paint.Align.LEFT
    }

    private val dimPaint = Paint().apply {
        color = 0x50000000
        style = Paint.Style.FILL
    }

    // Store computed rects for tap detection
    private val drawnRects = mutableListOf<Pair<RectF, TranslationResult>>()

    var translationResults: List<TranslationResult> = emptyList()
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        // Dim background
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), dimPaint)

        drawnRects.clear()

        val textSizeMultiplier = when (PrefsManager.textSize) {
            0 -> 0.8f
            2 -> 1.3f
            else -> 1f
        }

        val padding = 6f * density

        // First pass: draw all backgrounds (no borders, seamless look)
        val bgRects = mutableListOf<RectF>()
        val layouts = mutableListOf<StaticLayout>()
        val positions = mutableListOf<RectF>()

        for (result in translationResults) {
            val rect = result.boundingBox
            val rectF = RectF(rect)
            val boxWidth = rectF.width().toInt().coerceAtLeast(100)

            // Calculate text size to fit the box
            val boxHeight = rectF.height()
            val boxArea = boxHeight * boxWidth
            val textLen = result.translatedText.length.coerceAtLeast(1)
            // Estimate font size from available area
            val charArea = boxArea / textLen
            val fontSize = (Math.sqrt(charArea.toDouble()).toFloat() * 0.9f * textSizeMultiplier)
                .coerceIn(10f * density, 16f * density)
            textPaint.textSize = fontSize

            // Use StaticLayout for proper text wrapping with good line spacing
            val textWidth = (boxWidth - padding * 2).toInt().coerceAtLeast(50)
            val layout = StaticLayout.Builder
                .obtain(result.translatedText, 0, result.translatedText.length, textPaint, textWidth)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(4f * density, 1.2f)  // Extra spacing + 1.2x multiplier
                .setIncludePad(true)
                .build()

            val totalTextHeight = layout.height.toFloat()

            // Background rect covers original text area
            val bgRect = RectF(
                rectF.left - padding,
                rectF.top - padding,
                rectF.right + padding,
                (rectF.top + totalTextHeight + padding * 2).coerceAtLeast(rectF.bottom + padding)
            )

            bgRects.add(bgRect)
            layouts.add(layout)
            positions.add(rectF)
        }

        // Merge overlapping backgrounds for seamless look
        val mergedBgs = mergeOverlappingRects(bgRects)
        for (merged in mergedBgs) {
            canvas.drawRoundRect(merged, 4f * density, 4f * density, backgroundPaint)
        }

        // Second pass: draw text on top
        for (i in translationResults.indices) {
            val result = translationResults[i]
            val rectF = positions[i]
            val layout = layouts[i]
            val bgRect = bgRects[i]

            drawnRects.add(bgRect to result)

            canvas.save()
            canvas.translate(rectF.left, rectF.top + padding * 0.5f)
            layout.draw(canvas)
            canvas.restore()
        }
    }

    private fun mergeOverlappingRects(rects: List<RectF>): List<RectF> {
        if (rects.isEmpty()) return emptyList()
        val sorted = rects.sortedBy { it.top }
        val merged = mutableListOf(RectF(sorted[0]))
        val gap = 4f * density // Allow small gap between blocks to still merge

        for (i in 1 until sorted.size) {
            val current = sorted[i]
            val last = merged.last()

            // If this rect overlaps or is very close to the last merged rect
            if (current.top <= last.bottom + gap &&
                current.left < last.right + gap &&
                current.right > last.left - gap
            ) {
                // Merge: expand the last rect
                last.left = minOf(last.left, current.left)
                last.top = minOf(last.top, current.top)
                last.right = maxOf(last.right, current.right)
                last.bottom = maxOf(last.bottom, current.bottom)
            } else {
                merged.add(RectF(current))
            }
        }
        return merged
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            // Check if tapped on a translation block - copy it
            for ((bgRect, result) in drawnRects) {
                if (bgRect.contains(event.x, event.y)) {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("translation", result.translatedText))
                    Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
                    return true
                }
            }
            // Tapped outside - dismiss
            onDismiss()
            return true
        }
        return true
    }
}
