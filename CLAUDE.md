# Auto Screen Translate - Project Status

## What This App Does
Android app that captures the screen, extracts text via OCR, translates it on-device, and displays translated text as an overlay. Competitor to "Gaminik" (500K+ downloads). Free app monetized with AdMob ads.

## Tech Stack
- Kotlin + Jetpack Compose + Material3
- Google ML Kit Text Recognition (on-device OCR, FREE)
- Google ML Kit On-Device Translation (FREE, ~30MB per language model)
- MediaProjection API (screen capture, Play Store safe)
- Room (translation history database)
- Google AdMob (banner + interstitial)
- Dark theme with teal accent (#00BFA6)

## App Flow
1. User opens app → selects source language (or auto-detect) + target language
2. Downloads translation model (~30MB, one-time per language pair)
3. Taps "Start Translating" → grants permissions → floating bubble appears
4. Switches to any app (game, manga, chat, etc.)
5. Taps floating bubble → screen captured → OCR → translated → overlay shows translated text
6. Tap overlay to dismiss, tap bubble again for new translation

## Project Structure (42 files, all created)
```
com.autotranslate.app/
├── AutoTranslateApp.kt              - Application class (AdMob init, notification channel)
├── MainActivity.kt                  - Compose activity, permission flow, navigation
├── ads/AdManager.kt                 - AdMob banner + interstitial (TEST IDs currently)
├── data/
│   ├── Language.kt                  - 59 languages with ML Kit codes
│   ├── PrefsManager.kt              - SharedPreferences (lang pair, overlay settings)
│   ├── RecognizedBlock.kt           - OCR result data class
│   └── TranslationResult.kt         - Translation result with bounding boxes
├── db/
│   ├── AppDatabase.kt               - Room database singleton
│   ├── TranslationDao.kt            - DAO (insert, getAll, search, deleteAll)
│   └── TranslationHistoryEntity.kt  - Room entity
├── ocr/TextRecognizer.kt            - ML Kit text recognition wrapper
├── overlay/
│   ├── FloatingBubbleView.kt        - Draggable floating bubble (traditional View)
│   ├── OverlayWindowManager.kt      - WindowManager for bubble + overlay
│   └── TranslationOverlayView.kt    - Full-screen overlay showing translated text
├── service/
│   ├── OverlayService.kt            - Foreground service (core backbone)
│   ├── ScreenCaptureManager.kt      - MediaProjection + ImageReader
│   ├── ServiceStateHolder.kt        - Singleton StateFlow for activity↔service comm
│   └── TranslationPipeline.kt       - Capture → OCR → Translate orchestrator
├── translation/TranslatorManager.kt - ML Kit translation + model download
├── ui/
│   ├── components/
│   │   ├── BannerAd.kt              - Compose AdMob banner wrapper
│   │   ├── HistoryItem.kt           - Expandable history card
│   │   ├── LanguagePairCard.kt      - Source → Target language selector card
│   │   └── LanguageSelector.kt      - Bottom sheet language picker with search
│   ├── screens/
│   │   ├── HomeScreen.kt            - Main screen (lang pair, model status, start button)
│   │   ├── HistoryScreen.kt         - Translation history with search
│   │   └── SettingsScreen.kt        - Overlay settings, clear history, about
│   └── theme/Theme.kt               - Material3 dark theme (navy + teal)
└── viewmodel/MainViewModel.kt       - State management, model download, history
```

## What's DONE
- [x] Full project structure created (Gradle, manifest, resources, all packages)
- [x] Dark theme with teal accent color
- [x] 59 languages with ML Kit codes
- [x] HomeScreen with language pair card, model download, animated start button, glow effect
- [x] HistoryScreen with search and clear dialog
- [x] SettingsScreen (auto-detect toggle, overlay opacity slider, text size, clear history)
- [x] Language selector bottom sheet with search
- [x] OverlayService (foreground service with notification)
- [x] FloatingBubbleView (draggable, state changes: idle/processing)
- [x] OverlayWindowManager (manages bubble + translation overlay)
- [x] ScreenCaptureManager (MediaProjection + ImageReader)
- [x] TextRecognizer (ML Kit OCR wrapper)
- [x] TranslatorManager (ML Kit translation, model download, caching)
- [x] TranslationPipeline (capture → OCR → translate orchestrator)
- [x] TranslationOverlayView (shows translated text at bounding box positions)
- [x] Room database for translation history
- [x] AdMob integration (banner on home, interstitial every 5 translations)
- [x] Permission flow (overlay → notification → MediaProjection)
- [x] ServiceStateHolder for activity ↔ service communication
- [x] PrefsManager for all settings persistence
- [x] ProGuard rules for ML Kit + Room
- [x] App icon (adaptive icon with translate symbol)

## What's REMAINING (TODO)
- [ ] Open project in Android Studio and let Gradle sync
- [ ] Fix any compilation errors that come up during first build
- [ ] Test on physical Android device (emulator won't work for MediaProjection)
- [ ] Test full flow: permissions → bubble → capture → OCR → translate → overlay
- [ ] Replace test AdMob IDs with real ones (user's AdMob account: ca-app-pub-2039704869658818)
- [ ] Create proper app icon (generate with Gemini or design tool, replace placeholder)
- [ ] Create keystore for release signing
- [ ] Build release AAB for Google Play
- [ ] Create Play Store listing (screenshots, description, etc.)
- [ ] Optional: Add Japanese/Chinese/Korean OCR recognizers (currently Latin only)
- [ ] Optional: Add "auto-translate on screen change" mode (continuous)
- [ ] Optional: Add regional/area selection (crop region to translate instead of full screen)

## Key Config
- Package: `com.autotranslate.screen`
- Min SDK: 26 (Android 8.0)
- Target SDK: 35
- AdMob App ID (TEST): `ca-app-pub-3940256099942544~3347511713`
- Banner Ad ID (TEST): `ca-app-pub-3940256099942544/6300978111`
- Interstitial Ad ID (TEST): `ca-app-pub-3940256099942544/1033173712`
- JDK: 17 (Adoptium Temurin)

## Important Notes
- MediaProjection requires `startForeground()` BEFORE `getMediaProjection()` on Android 14+
- Floating bubble is hidden during screen capture to avoid capturing itself
- Each ML Kit translation model is ~30MB, downloaded on demand
- All translation runs on-device = zero API costs = all ad revenue is profit
- The `ic_translate.xml` tint is removed when used in FloatingBubbleView (tint set programmatically)
