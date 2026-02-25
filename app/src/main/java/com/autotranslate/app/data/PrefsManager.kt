package com.autotranslate.app.data

import android.content.Context
import android.content.SharedPreferences

object PrefsManager {
    private const val PREFS_NAME = "auto_translate_prefs"
    private const val KEY_SOURCE_LANG = "source_lang"
    private const val KEY_TARGET_LANG = "target_lang"
    private const val KEY_OVERLAY_OPACITY = "overlay_opacity"
    private const val KEY_TEXT_SIZE = "text_size"
    private const val KEY_AUTO_DETECT = "auto_detect"
    private const val KEY_TRANSLATION_COUNT = "translation_count_today"
    private const val KEY_LAST_COUNT_DATE = "last_count_date"
    private const val KEY_GUIDE_SHOWN = "guide_shown"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var sourceLangCode: String
        get() = prefs.getString(KEY_SOURCE_LANG, "auto") ?: "auto"
        set(value) = prefs.edit().putString(KEY_SOURCE_LANG, value).apply()

    var targetLangCode: String
        get() = prefs.getString(KEY_TARGET_LANG, "en") ?: "en"
        set(value) = prefs.edit().putString(KEY_TARGET_LANG, value).apply()

    var overlayOpacity: Float
        get() = prefs.getFloat(KEY_OVERLAY_OPACITY, 0.9f)
        set(value) = prefs.edit().putFloat(KEY_OVERLAY_OPACITY, value).apply()

    var textSize: Int
        get() = prefs.getInt(KEY_TEXT_SIZE, 1) // 0=small, 1=medium, 2=large
        set(value) = prefs.edit().putInt(KEY_TEXT_SIZE, value).apply()

    var autoDetect: Boolean
        get() = prefs.getBoolean(KEY_AUTO_DETECT, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_DETECT, value).apply()

    fun getTodayTranslationCount(): Int {
        val today = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.US)
            .format(java.util.Date())
        val lastDate = prefs.getString(KEY_LAST_COUNT_DATE, "") ?: ""
        if (lastDate != today) {
            prefs.edit()
                .putString(KEY_LAST_COUNT_DATE, today)
                .putInt(KEY_TRANSLATION_COUNT, 0)
                .apply()
            return 0
        }
        return prefs.getInt(KEY_TRANSLATION_COUNT, 0)
    }

    var guideShown: Boolean
        get() = prefs.getBoolean(KEY_GUIDE_SHOWN, false)
        set(value) = prefs.edit().putBoolean(KEY_GUIDE_SHOWN, value).apply()

    fun incrementTranslationCount() {
        val today = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.US)
            .format(java.util.Date())
        val lastDate = prefs.getString(KEY_LAST_COUNT_DATE, "") ?: ""
        val count = if (lastDate == today) {
            prefs.getInt(KEY_TRANSLATION_COUNT, 0) + 1
        } else {
            1
        }
        prefs.edit()
            .putString(KEY_LAST_COUNT_DATE, today)
            .putInt(KEY_TRANSLATION_COUNT, count)
            .apply()
    }
}
