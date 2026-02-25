package com.autotranslate.app.translation

import com.autotranslate.app.data.Language
import com.autotranslate.app.data.RecognizedBlock
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

class TranslatorManager {
    private val translatorCache = mutableMapOf<String, Translator>()
    private val modelManager = RemoteModelManager.getInstance()

    private fun getTranslator(source: Language, target: Language): Translator {
        val key = "${source.mlKitCode}->${target.mlKitCode}"
        return translatorCache.getOrPut(key) {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(source.mlKitCode)
                .setTargetLanguage(target.mlKitCode)
                .build()
            Translation.getClient(options)
        }
    }

    suspend fun isModelDownloaded(source: Language, target: Language): Boolean {
        return try {
            val models = modelManager.getDownloadedModels(TranslateRemoteModel::class.java).await()
            val downloadedCodes = models.map { it.language }.toSet()

            val sourceReady = source.code == "auto" || downloadedCodes.contains(source.mlKitCode)
            val targetReady = downloadedCodes.contains(target.mlKitCode)
            sourceReady && targetReady
        } catch (e: Exception) {
            false
        }
    }

    suspend fun ensureModelDownloaded(source: Language, target: Language) {
        val translator = getTranslator(
            if (source.code == "auto") Language.findByCode("en") else source,
            target
        )
        val conditions = DownloadConditions.Builder().build()
        translator.downloadModelIfNeeded(conditions).await()
    }

    suspend fun translate(text: String, source: Language, target: Language): String {
        val translator = getTranslator(
            if (source.code == "auto") Language.findByCode("en") else source,
            target
        )
        return translator.translate(text).await()
    }

    suspend fun translateBlocks(
        blocks: List<RecognizedBlock>,
        source: Language,
        target: Language,
    ): List<Pair<RecognizedBlock, String>> {
        val translator = getTranslator(
            if (source.code == "auto") Language.findByCode("en") else source,
            target
        )
        return blocks.map { block ->
            // Clean up text for better translation
            val cleanText = block.text
                .replace("-", " ")       // PUSH-UPS -> PUSH UPS
                .replace("\n", " ")      // Join lines into one sentence
                .replace(Regex("\\s+"), " ")  // Collapse whitespace
                .trim()
            val translated = translator.translate(cleanText).await()
            block to translated
        }
    }

    fun close() {
        translatorCache.values.forEach { it.close() }
        translatorCache.clear()
    }
}
