package com.autotranslate.app

import android.Manifest
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.autotranslate.app.ads.AdManager
import com.autotranslate.app.service.OverlayService
import com.autotranslate.app.ui.screens.HistoryScreen
import com.autotranslate.app.ui.screens.HomeScreen
import com.autotranslate.app.ui.screens.SettingsScreen
import com.autotranslate.app.ui.theme.AutoTranslateTheme
import com.autotranslate.app.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel
    private var pendingStartAfterPermission by mutableStateOf(false)

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Settings.canDrawOverlays(this)) {
            if (pendingStartAfterPermission) {
                pendingStartAfterPermission = false
                requestMediaProjection()
            }
        } else {
            Toast.makeText(this, "Overlay permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted || Build.VERSION.SDK_INT < 33) {
            requestMediaProjection()
        }
    }

    private val mediaProjectionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            startOverlayService(result.resultCode, result.data!!)
        } else {
            Toast.makeText(this, "Screen capture permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        AdManager.loadInterstitial(this)

        setContent {
            AutoTranslateTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToHistory = { navController.navigate("history") },
                            onNavigateToSettings = { navController.navigate("settings") },
                            onStartTranslating = { startTranslating() },
                            onStopTranslating = { stopTranslating() },
                        )
                    }
                    composable("history") {
                        HistoryScreen(
                            viewModel = viewModel,
                            onNavigateBack = { navController.popBackStack() },
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            viewModel = viewModel,
                            onNavigateBack = { navController.popBackStack() },
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshTranslationCount()
    }

    private fun startTranslating() {
        // Step 1: Check overlay permission
        if (!Settings.canDrawOverlays(this)) {
            pendingStartAfterPermission = true
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            overlayPermissionLauncher.launch(intent)
            return
        }

        // Step 2: Check notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return
        }

        // Step 3: Request MediaProjection
        requestMediaProjection()
    }

    private fun requestMediaProjection() {
        val projectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjectionLauncher.launch(projectionManager.createScreenCaptureIntent())
    }

    private fun startOverlayService(resultCode: Int, data: Intent) {
        val serviceIntent = Intent(this, OverlayService::class.java).apply {
            action = OverlayService.ACTION_START
            putExtra(OverlayService.EXTRA_RESULT_CODE, resultCode)
            putExtra(OverlayService.EXTRA_RESULT_DATA, data)
        }
        startForegroundService(serviceIntent)
    }

    private fun stopTranslating() {
        val serviceIntent = Intent(this, OverlayService::class.java).apply {
            action = OverlayService.ACTION_STOP
        }
        startService(serviceIntent)
    }
}
