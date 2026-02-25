package com.autotranslate.app.data

import android.graphics.Rect

data class RecognizedBlock(
    val text: String,
    val boundingBox: Rect,
    val confidence: Float,
    val language: String,
)
