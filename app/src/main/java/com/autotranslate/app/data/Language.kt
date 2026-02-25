package com.autotranslate.app.data

import com.google.mlkit.nl.translate.TranslateLanguage

data class Language(
    val code: String,
    val mlKitCode: String,
    val displayName: String,
    val nativeName: String,
) {
    companion object {
        val AUTO_DETECT = Language("auto", "", "Auto-detect", "Auto")

        val ALL = listOf(
            Language("af", TranslateLanguage.AFRIKAANS, "Afrikaans", "Afrikaans"),
            Language("ar", TranslateLanguage.ARABIC, "Arabic", "\u0627\u0644\u0639\u0631\u0628\u064A\u0629"),
            Language("be", TranslateLanguage.BELARUSIAN, "Belarusian", "\u0411\u0435\u043B\u0430\u0440\u0443\u0441\u043A\u0430\u044F"),
            Language("bg", TranslateLanguage.BULGARIAN, "Bulgarian", "\u0411\u044A\u043B\u0433\u0430\u0440\u0441\u043A\u0438"),
            Language("bn", TranslateLanguage.BENGALI, "Bengali", "\u09AC\u09BE\u0982\u09B2\u09BE"),
            Language("ca", TranslateLanguage.CATALAN, "Catalan", "Catal\u00E0"),
            Language("cs", TranslateLanguage.CZECH, "Czech", "\u010Ce\u0161tina"),
            Language("cy", TranslateLanguage.WELSH, "Welsh", "Cymraeg"),
            Language("da", TranslateLanguage.DANISH, "Danish", "Dansk"),
            Language("de", TranslateLanguage.GERMAN, "German", "Deutsch"),
            Language("el", TranslateLanguage.GREEK, "Greek", "\u0395\u03BB\u03BB\u03B7\u03BD\u03B9\u03BA\u03AC"),
            Language("en", TranslateLanguage.ENGLISH, "English", "English"),
            Language("eo", TranslateLanguage.ESPERANTO, "Esperanto", "Esperanto"),
            Language("es", TranslateLanguage.SPANISH, "Spanish", "Espa\u00F1ol"),
            Language("et", TranslateLanguage.ESTONIAN, "Estonian", "Eesti"),
            Language("fa", TranslateLanguage.PERSIAN, "Persian", "\u0641\u0627\u0631\u0633\u06CC"),
            Language("fi", TranslateLanguage.FINNISH, "Finnish", "Suomi"),
            Language("fr", TranslateLanguage.FRENCH, "French", "Fran\u00E7ais"),
            Language("ga", TranslateLanguage.IRISH, "Irish", "Gaeilge"),
            Language("gl", TranslateLanguage.GALICIAN, "Galician", "Galego"),
            Language("gu", TranslateLanguage.GUJARATI, "Gujarati", "\u0A97\u0AC1\u0A9C\u0AB0\u0ABE\u0AA4\u0AC0"),
            Language("he", TranslateLanguage.HEBREW, "Hebrew", "\u05E2\u05D1\u05E8\u05D9\u05EA"),
            Language("hi", TranslateLanguage.HINDI, "Hindi", "\u0939\u093F\u0928\u094D\u0926\u0940"),
            Language("hr", TranslateLanguage.CROATIAN, "Croatian", "Hrvatski"),
            Language("ht", TranslateLanguage.HAITIAN_CREOLE, "Haitian Creole", "Krey\u00F2l"),
            Language("hu", TranslateLanguage.HUNGARIAN, "Hungarian", "Magyar"),
            Language("id", TranslateLanguage.INDONESIAN, "Indonesian", "Indonesia"),
            Language("is", TranslateLanguage.ICELANDIC, "Icelandic", "\u00CDslenska"),
            Language("it", TranslateLanguage.ITALIAN, "Italian", "Italiano"),
            Language("ja", TranslateLanguage.JAPANESE, "Japanese", "\u65E5\u672C\u8A9E"),
            Language("ka", TranslateLanguage.GEORGIAN, "Georgian", "\u10E5\u10D0\u10E0\u10D7\u10E3\u10DA\u10D8"),
            Language("kn", TranslateLanguage.KANNADA, "Kannada", "\u0C95\u0CA8\u0CCD\u0CA8\u0CA1"),
            Language("ko", TranslateLanguage.KOREAN, "Korean", "\uD55C\uAD6D\uC5B4"),
            Language("lt", TranslateLanguage.LITHUANIAN, "Lithuanian", "Lietuvi\u0173"),
            Language("lv", TranslateLanguage.LATVIAN, "Latvian", "Latvie\u0161u"),
            Language("mk", TranslateLanguage.MACEDONIAN, "Macedonian", "\u041C\u0430\u043A\u0435\u0434\u043E\u043D\u0441\u043A\u0438"),
            Language("mr", TranslateLanguage.MARATHI, "Marathi", "\u092E\u0930\u093E\u0920\u0940"),
            Language("ms", TranslateLanguage.MALAY, "Malay", "Melayu"),
            Language("mt", TranslateLanguage.MALTESE, "Maltese", "Malti"),
            Language("nl", TranslateLanguage.DUTCH, "Dutch", "Nederlands"),
            Language("no", TranslateLanguage.NORWEGIAN, "Norwegian", "Norsk"),
            Language("pl", TranslateLanguage.POLISH, "Polish", "Polski"),
            Language("pt", TranslateLanguage.PORTUGUESE, "Portuguese", "Portugu\u00EAs"),
            Language("ro", TranslateLanguage.ROMANIAN, "Romanian", "Rom\u00E2n\u0103"),
            Language("ru", TranslateLanguage.RUSSIAN, "Russian", "\u0420\u0443\u0441\u0441\u043A\u0438\u0439"),
            Language("sk", TranslateLanguage.SLOVAK, "Slovak", "Sloven\u010Dina"),
            Language("sl", TranslateLanguage.SLOVENIAN, "Slovenian", "Sloven\u0161\u010Dina"),
            Language("sq", TranslateLanguage.ALBANIAN, "Albanian", "Shqip"),
            Language("sv", TranslateLanguage.SWEDISH, "Swedish", "Svenska"),
            Language("sw", TranslateLanguage.SWAHILI, "Swahili", "Kiswahili"),
            Language("ta", TranslateLanguage.TAMIL, "Tamil", "\u0BA4\u0BAE\u0BBF\u0BB4\u0BCD"),
            Language("te", TranslateLanguage.TELUGU, "Telugu", "\u0C24\u0C46\u0C32\u0C41\u0C17\u0C41"),
            Language("th", TranslateLanguage.THAI, "Thai", "\u0E44\u0E17\u0E22"),
            Language("tl", TranslateLanguage.TAGALOG, "Tagalog", "Tagalog"),
            Language("tr", TranslateLanguage.TURKISH, "Turkish", "T\u00FCrk\u00E7e"),
            Language("uk", TranslateLanguage.UKRAINIAN, "Ukrainian", "\u0423\u043A\u0440\u0430\u0457\u043D\u0441\u044C\u043A\u0430"),
            Language("ur", TranslateLanguage.URDU, "Urdu", "\u0627\u0631\u062F\u0648"),
            Language("vi", TranslateLanguage.VIETNAMESE, "Vietnamese", "Ti\u1EBFng Vi\u1EC7t"),
            Language("zh", TranslateLanguage.CHINESE, "Chinese", "\u4E2D\u6587"),
        )

        fun findByCode(code: String): Language {
            if (code == "auto") return AUTO_DETECT
            return ALL.find { it.code == code } ?: ALL.first { it.code == "en" }
        }
    }
}
