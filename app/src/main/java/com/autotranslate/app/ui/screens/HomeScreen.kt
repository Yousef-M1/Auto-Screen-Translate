package com.autotranslate.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autotranslate.app.data.PrefsManager
import com.autotranslate.app.ui.components.BannerAd
import com.autotranslate.app.ui.components.GuideDialog
import com.autotranslate.app.ui.components.LanguagePairCard
import com.autotranslate.app.ui.components.LanguageSelectorSheet
import com.autotranslate.app.ui.theme.AccentGreen
import com.autotranslate.app.ui.theme.AccentRed
import com.autotranslate.app.ui.theme.AccentTeal
import com.autotranslate.app.ui.theme.AccentYellow
import com.autotranslate.app.ui.theme.DarkBackground
import com.autotranslate.app.ui.theme.DarkCard
import com.autotranslate.app.ui.theme.TextMuted
import com.autotranslate.app.ui.theme.TextPrimary
import com.autotranslate.app.ui.theme.TextSecondary
import com.autotranslate.app.viewmodel.MainViewModel
import com.autotranslate.app.viewmodel.ModelState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onStartTranslating: () -> Unit,
    onStopTranslating: () -> Unit,
) {
    val sourceLang by viewModel.sourceLang.collectAsState()
    val targetLang by viewModel.targetLang.collectAsState()
    val isRunning by viewModel.isServiceRunning.collectAsState()
    val modelState by viewModel.modelState.collectAsState()
    val translationCount by viewModel.translationCount.collectAsState()

    var showSourcePicker by remember { mutableStateOf(false) }
    var showTargetPicker by remember { mutableStateOf(false) }
    var showGuide by remember { mutableStateOf(!PrefsManager.guideShown) }

    val buttonScale by animateFloatAsState(
        targetValue = if (isRunning) 0.95f else 1f,
        animationSpec = tween(300),
        label = "buttonScale"
    )
    val buttonColor by animateColorAsState(
        targetValue = if (isRunning) AccentRed else AccentTeal,
        animationSpec = tween(300),
        label = "buttonColor"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Translate,
                        contentDescription = null,
                        tint = AccentTeal,
                        modifier = Modifier.size(28.dp),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Auto Translate",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            actions = {
                IconButton(onClick = { showGuide = true }) {
                    Icon(Icons.Default.HelpOutline, "Guide", tint = TextSecondary)
                }
                IconButton(onClick = onNavigateToHistory) {
                    Icon(Icons.Default.History, "History", tint = TextSecondary)
                }
                IconButton(onClick = onNavigateToSettings) {
                    Icon(Icons.Default.Settings, "Settings", tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = DarkBackground,
                titleContentColor = TextPrimary,
            ),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Language pair card
            LanguagePairCard(
                sourceLanguage = sourceLang,
                targetLanguage = targetLang,
                onSourceClick = { showSourcePicker = true },
                onTargetClick = { showTargetPicker = true },
                onSwapClick = { viewModel.swapLanguages() },
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Model status
            when (val state = modelState) {
                is ModelState.Checking -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkCard)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = AccentTeal,
                            strokeWidth = 2.dp,
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Checking model status...", color = TextSecondary)
                    }
                }

                is ModelState.NotDownloaded -> {
                    Button(
                        onClick = { viewModel.downloadModel() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentYellow.copy(alpha = 0.15f),
                            contentColor = AccentYellow,
                        ),
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Download Language Model (~30MB)")
                    }
                }

                is ModelState.Downloading -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(AccentYellow.copy(alpha = 0.1f))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = AccentYellow,
                            strokeWidth = 2.dp,
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Downloading model...", color = AccentYellow)
                    }
                }

                is ModelState.Ready -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(AccentGreen.copy(alpha = 0.1f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text("Model ready", color = AccentGreen, fontWeight = FontWeight.Medium)
                    }
                }

                is ModelState.Error -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(AccentRed.copy(alpha = 0.1f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "Error: ${state.message}",
                            color = AccentRed,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Main action button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(bottom = 16.dp),
            ) {
                // Glow effect
                if (!isRunning) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        AccentTeal.copy(alpha = 0.3f),
                                        AccentTeal.copy(alpha = 0f),
                                    )
                                )
                            )
                    )
                }

                Button(
                    onClick = {
                        if (isRunning) onStopTranslating() else onStartTranslating()
                    },
                    modifier = Modifier
                        .size(130.dp)
                        .scale(buttonScale),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    enabled = modelState is ModelState.Ready,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            if (isRunning) Icons.Default.Stop else Icons.Default.Translate,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            tint = DarkBackground,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            if (isRunning) "STOP" else "START",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBackground,
                        )
                    }
                }
            }

            // Status text
            Text(
                text = if (isRunning) "Tap the floating bubble to translate"
                else "Tap START to begin",
                color = TextMuted,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Translation count
            if (translationCount > 0) {
                Text(
                    text = "$translationCount translations today",
                    color = TextMuted,
                    style = MaterialTheme.typography.labelMedium,
                )
            }

            Spacer(modifier = Modifier.weight(0.5f))
        }

        // Banner ad at bottom
        BannerAd(modifier = Modifier.fillMaxWidth())
    }

    // First-time guide
    if (showGuide) {
        GuideDialog(onDismiss = {
            showGuide = false
            PrefsManager.guideShown = true
        })
    }

    // Language picker sheets
    if (showSourcePicker) {
        LanguageSelectorSheet(
            title = "Source Language",
            showAutoDetect = true,
            selectedLanguage = sourceLang,
            onLanguageSelected = { viewModel.setSourceLanguage(it) },
            onDismiss = { showSourcePicker = false },
        )
    }

    if (showTargetPicker) {
        LanguageSelectorSheet(
            title = "Target Language",
            showAutoDetect = false,
            selectedLanguage = targetLang,
            onLanguageSelected = { viewModel.setTargetLanguage(it) },
            onDismiss = { showTargetPicker = false },
        )
    }
}
