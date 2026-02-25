package com.autotranslate.app.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translation_history")
data class TranslationHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val originalText: String,
    val translatedText: String,
    val sourceLangCode: String,
    val targetLangCode: String,
    val timestamp: Long,
)
