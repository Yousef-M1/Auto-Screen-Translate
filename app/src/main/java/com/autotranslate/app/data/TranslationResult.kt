package com.autotranslate.app.data

import android.graphics.Rect

data class TranslationResult(
    val originalText: String,
    val translatedText: String,
    val boundingBox: Rect,
    val sourceLang: String,
    val targetLang: String,
    val confidence: Float,
    val timestamp: Long,
)
