package com.autotranslate.app.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Handler
import android.os.HandlerThread
import android.util.DisplayMetrics
import android.view.WindowManager
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume

class ScreenCaptureManager(
    private val context: Context,
    private val mediaProjection: MediaProjection,
) {
    private val handlerThread = HandlerThread("ScreenCapture").also { it.start() }
    private val handler = Handler(handlerThread.looper)

    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    private val screenWidth: Int
    private val screenHeight: Int
    private val screenDensity: Int

    fun getScreenWidth(): Int = screenWidth

    init {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        wm.defaultDisplay.getRealMetrics(metrics)
        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels
        screenDensity = metrics.densityDpi
    }

    suspend fun captureScreen(): Bitmap? {
        return withTimeout(5000) {
            suspendCancellableCoroutine { continuation ->
                try {
                    // Clean up previous capture
                    releaseVirtualDisplay()

                    val reader = ImageReader.newInstance(
                        screenWidth, screenHeight,
                        PixelFormat.RGBA_8888, 2
                    )
                    imageReader = reader

                    var captured = false

                    reader.setOnImageAvailableListener({ imgReader ->
                        if (captured) return@setOnImageAvailableListener
                        captured = true

                        val image = imgReader.acquireLatestImage()
                        if (image != null) {
                            val bitmap = imageToBitmap(image)
                            image.close()
                            releaseVirtualDisplay()
                            if (continuation.isActive) {
                                continuation.resume(bitmap)
                            }
                        } else {
                            releaseVirtualDisplay()
                            if (continuation.isActive) {
                                continuation.resume(null)
                            }
                        }
                    }, handler)

                    virtualDisplay = mediaProjection.createVirtualDisplay(
                        "ScreenCapture",
                        screenWidth, screenHeight, screenDensity,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        reader.surface,
                        null, handler
                    )

                    continuation.invokeOnCancellation {
                        releaseVirtualDisplay()
                    }
                } catch (e: Exception) {
                    releaseVirtualDisplay()
                    if (continuation.isActive) {
                        continuation.resume(null)
                    }
                }
            }
        }
    }

    private fun imageToBitmap(image: Image): Bitmap {
        val planes = image.planes
        val buffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * screenWidth

        val bitmap = Bitmap.createBitmap(
            screenWidth + rowPadding / pixelStride,
            screenHeight,
            Bitmap.Config.ARGB_8888
        )
        bitmap.copyPixelsFromBuffer(buffer)

        // Crop to exact screen size if needed
        return if (bitmap.width != screenWidth) {
            val cropped = Bitmap.createBitmap(bitmap, 0, 0, screenWidth, screenHeight)
            bitmap.recycle()
            cropped
        } else {
            bitmap
        }
    }

    private fun releaseVirtualDisplay() {
        virtualDisplay?.release()
        virtualDisplay = null
        imageReader?.close()
        imageReader = null
    }

    fun release() {
        releaseVirtualDisplay()
        handlerThread.quitSafely()
    }
}
