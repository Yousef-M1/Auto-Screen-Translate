package com.autotranslate.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.autotranslate.app.data.Language
import com.autotranslate.app.data.PrefsManager
import com.autotranslate.app.db.AppDatabase
import com.autotranslate.app.db.TranslationHistoryEntity
import com.autotranslate.app.service.ServiceStateHolder
import com.autotranslate.app.translation.TranslatorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    val translatorManager = TranslatorManager()

    private val _sourceLang = MutableStateFlow(
        Language.findByCode(PrefsManager.sourceLangCode)
    )
    val sourceLang: StateFlow<Language> = _sourceLang.asStateFlow()

    private val _targetLang = MutableStateFlow(
        Language.findByCode(PrefsManager.targetLangCode)
    )
    val targetLang: StateFlow<Language> = _targetLang.asStateFlow()

    private val _translationCount = MutableStateFlow(PrefsManager.getTodayTranslationCount())
    val translationCount: StateFlow<Int> = _translationCount.asStateFlow()

    private val _modelState = MutableStateFlow<ModelState>(ModelState.Checking)
    val modelState: StateFlow<ModelState> = _modelState.asStateFlow()

    val history: StateFlow<List<TranslationHistoryEntity>> = db.translationDao().getAll()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val isServiceRunning = ServiceStateHolder.isRunning

    init {
        // Sync ServiceStateHolder with saved preferences
        ServiceStateHolder.sourceLang.value = _sourceLang.value
        ServiceStateHolder.targetLang.value = _targetLang.value
        checkModelStatus()
    }

    fun setSourceLanguage(lang: Language) {
        _sourceLang.value = lang
        PrefsManager.sourceLangCode = lang.code
        ServiceStateHolder.sourceLang.value = lang
        checkModelStatus()
    }

    fun setTargetLanguage(lang: Language) {
        _targetLang.value = lang
        PrefsManager.targetLangCode = lang.code
        ServiceStateHolder.targetLang.value = lang
        checkModelStatus()
    }

    fun swapLanguages() {
        val source = _sourceLang.value
        val target = _targetLang.value
        if (source.code == "auto") return
        setSourceLanguage(target)
        setTargetLanguage(source)
    }

    fun checkModelStatus() {
        val source = _sourceLang.value
        val target = _targetLang.value
        if (source.code == "auto") {
            _modelState.value = ModelState.Ready
            return
        }
        viewModelScope.launch {
            _modelState.value = ModelState.Checking
            val ready = translatorManager.isModelDownloaded(source, target)
            _modelState.value = if (ready) ModelState.Ready else ModelState.NotDownloaded
        }
    }

    fun downloadModel() {
        val source = _sourceLang.value
        val target = _targetLang.value
        viewModelScope.launch {
            _modelState.value = ModelState.Downloading
            try {
                translatorManager.ensureModelDownloaded(source, target)
                _modelState.value = ModelState.Ready
            } catch (e: Exception) {
                _modelState.value = ModelState.Error(e.message ?: "Download failed")
            }
        }
    }

    fun refreshTranslationCount() {
        _translationCount.value = PrefsManager.getTodayTranslationCount()
    }

    fun clearHistory() {
        viewModelScope.launch {
            db.translationDao().deleteAll()
        }
    }
}

sealed class ModelState {
    data object Checking : ModelState()
    data object NotDownloaded : ModelState()
    data object Downloading : ModelState()
    data object Ready : ModelState()
    data class Error(val message: String) : ModelState()
}
