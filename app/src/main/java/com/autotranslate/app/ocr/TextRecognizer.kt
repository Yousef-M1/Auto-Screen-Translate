package com.autotranslate.app.ocr

import android.graphics.Bitmap
import android.graphics.Rect
import com.autotranslate.app.data.RecognizedBlock
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

class TextRecognizer {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun recognizeText(bitmap: Bitmap): List<RecognizedBlock> {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        val visionText = recognizer.process(inputImage).await()

        // Use block-level text for better translation context
        return visionText.textBlocks.map { block ->
            val avgConfidence = block.lines
                .mapNotNull { it.confidence }
                .average()
                .toFloat()
                .takeIf { !it.isNaN() } ?: 0f

            RecognizedBlock(
                text = block.text,
                boundingBox = block.boundingBox ?: Rect(),
                confidence = avgConfidence,
                language = block.recognizedLanguage ?: "und",
            )
        }
    }

    fun close() {
        recognizer.close()
    }
}
