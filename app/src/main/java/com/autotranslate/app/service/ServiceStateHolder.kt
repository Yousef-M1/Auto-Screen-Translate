package com.autotranslate.app.service

import com.autotranslate.app.data.Language
import com.autotranslate.app.data.PrefsManager
import com.autotranslate.app.data.TranslationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ServiceStateHolder {
    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _pipelineState = MutableStateFlow(PipelineState.IDLE)
    val pipelineState: StateFlow<PipelineState> = _pipelineState.asStateFlow()

    private val _lastResults = MutableStateFlow<List<TranslationResult>?>(null)
    val lastResults: StateFlow<List<TranslationResult>?> = _lastResults.asStateFlow()

    val sourceLang = MutableStateFlow(Language.findByCode(PrefsManager.sourceLangCode))
    val targetLang = MutableStateFlow(Language.findByCode(PrefsManager.targetLangCode))

    fun setRunning(running: Boolean) {
        _isRunning.value = running
    }

    fun setPipelineState(state: PipelineState) {
        _pipelineState.value = state
    }

    fun setLastResults(results: List<TranslationResult>?) {
        _lastResults.value = results
    }
}

enum class PipelineState {
    IDLE,
    CAPTURING,
    RECOGNIZING,
    TRANSLATING,
    DONE,
    ERROR,
}
