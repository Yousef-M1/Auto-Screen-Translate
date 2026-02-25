package com.autotranslate.app.service

import android.graphics.Rect
import android.util.Log
import com.autotranslate.app.data.Language
import com.autotranslate.app.data.TranslationResult
import com.autotranslate.app.ocr.TextRecognizer
import com.autotranslate.app.translation.TranslatorManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TranslationPipeline(
    private val screenCaptureManager: ScreenCaptureManager,
) {
    private val textRecognizer = TextRecognizer()
    private val translatorManager = TranslatorManager()
    private var screenHeight = 2340 // default, updated on capture

    suspend fun execute(
        sourceLang: Language,
        targetLang: Language,
    ): Result<List<TranslationResult>> = withContext(Dispatchers.Default) {
        try {
            Log.d(TAG, "Pipeline start: ${sourceLang.code} -> ${targetLang.code}")

            // Step 1: Capture screen
            ServiceStateHolder.setPipelineState(PipelineState.CAPTURING)
            val bitmap = screenCaptureManager.captureScreen()
            if (bitmap == null) {
                Log.e(TAG, "Screen capture returned null")
                return@withContext Result.failure(Exception("Screen capture failed"))
            }
            screenHeight = bitmap.height
            Log.d(TAG, "Captured: ${bitmap.width}x${bitmap.height}")

            // Step 2: OCR text recognition
            ServiceStateHolder.setPipelineState(PipelineState.RECOGNIZING)
            val recognizedBlocks = textRecognizer.recognizeText(bitmap)
            bitmap.recycle()
            Log.d(TAG, "OCR found ${recognizedBlocks.size} blocks")

            // Filter out low-confidence, short text, numbers-only, and UI elements
            val filteredBlocks = recognizedBlocks.filter { block ->
                block.confidence >= 0.5f &&
                block.text.length >= 3 &&
                !block.text.matches(Regex("^[\\d:%.\\s/|,]+$")) && // Skip numbers, time, percentages
                !isStatusBarArea(block.boundingBox, screenHeight) &&
                !isNavigationBarArea(block.boundingBox, screenHeight) &&
                !isSmallUIElement(block)
            }
            Log.d(TAG, "After filter: ${filteredBlocks.size} blocks")
            for (block in filteredBlocks) {
                Log.d(TAG, "  OCR text: '${block.text}' (conf=${block.confidence})")
            }
            if (filteredBlocks.isEmpty()) {
                return@withContext Result.failure(Exception("No text detected on screen"))
            }

            // Step 3: Translate
            ServiceStateHolder.setPipelineState(PipelineState.TRANSLATING)

            // Ensure model is downloaded
            try {
                translatorManager.ensureModelDownloaded(sourceLang, targetLang)
                Log.d(TAG, "Model ready")
            } catch (e: Exception) {
                Log.e(TAG, "Model download failed", e)
                return@withContext Result.failure(Exception("Translation model not available: ${e.message}"))
            }

            val translatedBlocks = translatorManager.translateBlocks(
                filteredBlocks, sourceLang, targetLang
            )
            for ((original, translated) in translatedBlocks) {
                Log.d(TAG, "  Translated: '${original.text}' -> '$translated'")
            }

            // Step 4: Build results
            val results = translatedBlocks.map { (original, translated) ->
                TranslationResult(
                    originalText = original.text,
                    translatedText = translated,
                    boundingBox = original.boundingBox,
                    sourceLang = sourceLang.code,
                    targetLang = targetLang.code,
                    confidence = original.confidence,
                    timestamp = System.currentTimeMillis(),
                )
            }

            Log.d(TAG, "Pipeline complete: ${results.size} results")
            Result.success(results)
        } catch (e: Exception) {
            Log.e(TAG, "Pipeline FAILED", e)
            Result.failure(e)
        }
    }

    private fun isStatusBarArea(rect: Rect, height: Int): Boolean {
        // Top ~8% of screen is status bar + browser toolbar
        return rect.top < height * 0.08
    }

    private fun isNavigationBarArea(rect: Rect, height: Int): Boolean {
        // Bottom ~10% of screen is navigation bar + app bar
        return rect.bottom > height * 0.90
    }

    private fun isSmallUIElement(block: com.autotranslate.app.data.RecognizedBlock): Boolean {
        val rect = block.boundingBox
        val boxHeight = rect.height()
        val textLen = block.text.trim().length
        // Small UI buttons: short text in small boxes (like "Share", "Edit", "YouTube", etc.)
        return textLen <= 10 && boxHeight < screenHeight * 0.04
    }

    companion object {
        private const val TAG = "TranslationPipeline"
    }

    fun close() {
        textRecognizer.close()
        translatorManager.close()
    }
}
