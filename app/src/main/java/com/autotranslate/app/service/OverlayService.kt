package com.autotranslate.app.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.autotranslate.app.AutoTranslateApp
import com.autotranslate.app.MainActivity
import com.autotranslate.app.R
import com.autotranslate.app.data.PrefsManager
import com.autotranslate.app.db.AppDatabase
import com.autotranslate.app.db.TranslationHistoryEntity
import com.autotranslate.app.overlay.FloatingBubbleView
import com.autotranslate.app.overlay.OverlayWindowManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OverlayService : Service() {

    companion object {
        private const val TAG = "OverlayService"
        const val ACTION_START = "com.autotranslate.START"
        const val ACTION_STOP = "com.autotranslate.STOP"
        const val EXTRA_RESULT_CODE = "result_code"
        const val EXTRA_RESULT_DATA = "result_data"
        private const val NOTIFICATION_ID = 1001
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var overlayManager: OverlayWindowManager
    private var screenCaptureManager: ScreenCaptureManager? = null
    private var translationPipeline: TranslationPipeline? = null
    private var mediaProjection: MediaProjection? = null
    private var isCapturing = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        overlayManager = OverlayWindowManager(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: action=${intent?.action}, intent=$intent")

        // MUST call startForeground() immediately - Android kills the app if we don't
        try {
            if (Build.VERSION.SDK_INT >= 34) {
                startForeground(
                    NOTIFICATION_ID,
                    createNotification(),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                )
            } else {
                startForeground(NOTIFICATION_ID, createNotification())
            }
            Log.d(TAG, "startForeground() succeeded")
        } catch (e: Exception) {
            Log.e(TAG, "startForeground() FAILED", e)
            stopSelf()
            return START_NOT_STICKY
        }

        if (intent == null) {
            Log.w(TAG, "intent is null, stopping")
            stopSelf()
            return START_NOT_STICKY
        }

        when (intent.action) {
            ACTION_START -> {
                val resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, Int.MIN_VALUE)
                @Suppress("DEPRECATION")
                val resultData: Intent? = intent.getParcelableExtra(EXTRA_RESULT_DATA)
                Log.d(TAG, "ACTION_START: resultCode=$resultCode, resultData=$resultData")

                if (resultCode == Int.MIN_VALUE || resultData == null) {
                    Log.e(TAG, "Invalid resultCode or resultData is null")
                    stopSelf()
                    return START_NOT_STICKY
                }

                try {
                    val projectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                    mediaProjection = projectionManager.getMediaProjection(resultCode, resultData)
                    Log.d(TAG, "MediaProjection: $mediaProjection")
                } catch (e: Exception) {
                    Log.e(TAG, "getMediaProjection FAILED", e)
                    stopSelf()
                    return START_NOT_STICKY
                }

                if (mediaProjection == null) {
                    Log.e(TAG, "mediaProjection is null")
                    stopSelf()
                    return START_NOT_STICKY
                }

                val captureManager = ScreenCaptureManager(this, mediaProjection!!)
                screenCaptureManager = captureManager

                val pipeline = TranslationPipeline(captureManager)
                translationPipeline = pipeline

                try {
                    overlayManager.showBubble { onBubbleTapped() }
                    Log.d(TAG, "Bubble shown successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "showBubble FAILED", e)
                }
                ServiceStateHolder.setRunning(true)
                Log.d(TAG, "Service started and running")
            }

            ACTION_STOP -> {
                Log.d(TAG, "ACTION_STOP received")
                stopService()
            }

            else -> {
                Log.w(TAG, "Unknown action: ${intent.action}")
            }
        }
        return START_NOT_STICKY
    }

    private fun onBubbleTapped() {
        if (isCapturing) return
        isCapturing = true

        overlayManager.hideTranslationOverlay()
        overlayManager.setBubbleState(FloatingBubbleView.BubbleState.PROCESSING)
        ServiceStateHolder.setPipelineState(PipelineState.CAPTURING)

        scope.launch {
            try {
                // Hide bubble before capture
                overlayManager.hideBubble()
                delay(100)

                val sourceLang = ServiceStateHolder.sourceLang.value
                val targetLang = ServiceStateHolder.targetLang.value

                val result = translationPipeline?.execute(sourceLang, targetLang)

                overlayManager.showBubbleAgain()

                result?.fold(
                    onSuccess = { results ->
                        if (results.isNotEmpty()) {
                            overlayManager.showTranslationOverlay(results)
                            ServiceStateHolder.setLastResults(results)
                            ServiceStateHolder.setPipelineState(PipelineState.DONE)

                            // Save to history
                            saveToHistory(results)
                            PrefsManager.incrementTranslationCount()
                        } else {
                            ServiceStateHolder.setPipelineState(PipelineState.ERROR)
                        }
                    },
                    onFailure = {
                        ServiceStateHolder.setPipelineState(PipelineState.ERROR)
                    }
                )
            } catch (e: Exception) {
                overlayManager.showBubbleAgain()
                ServiceStateHolder.setPipelineState(PipelineState.ERROR)
            } finally {
                overlayManager.setBubbleState(FloatingBubbleView.BubbleState.IDLE)
                isCapturing = false
            }
        }
    }

    private fun saveToHistory(results: List<com.autotranslate.app.data.TranslationResult>) {
        val db = AppDatabase.getInstance(this)
        scope.launch(Dispatchers.IO) {
            val combined = results.joinToString("\n") { it.originalText }
            val translated = results.joinToString("\n") { it.translatedText }
            db.translationDao().insert(
                TranslationHistoryEntity(
                    originalText = combined,
                    translatedText = translated,
                    sourceLangCode = results.first().sourceLang,
                    targetLangCode = results.first().targetLang,
                    timestamp = System.currentTimeMillis(),
                )
            )
        }
    }

    private fun createNotification(): Notification {
        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = PendingIntent.getService(
            this, 0,
            Intent(this, OverlayService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, AutoTranslateApp.CHANNEL_ID)
            .setContentTitle("Auto Translate is ready")
            .setContentText("Tap the floating bubble to translate")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(openIntent)
            .addAction(R.drawable.ic_notification, "Stop", stopIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun stopService() {
        ServiceStateHolder.setRunning(false)
        ServiceStateHolder.setPipelineState(PipelineState.IDLE)
        overlayManager.removeAll()
        screenCaptureManager?.release()
        mediaProjection?.stop()
        mediaProjection = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        stopService()
        scope.cancel()
        super.onDestroy()
    }
}
