package com.autotranslate.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Screenshot
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.autotranslate.app.ui.theme.AccentTeal
import com.autotranslate.app.ui.theme.DarkBackground
import com.autotranslate.app.ui.theme.DarkCard
import com.autotranslate.app.ui.theme.TextMuted
import com.autotranslate.app.ui.theme.TextPrimary
import com.autotranslate.app.ui.theme.TextSecondary

private data class GuideStep(
    val icon: ImageVector,
    val title: String,
    val description: String,
)

private val guideSteps = listOf(
    GuideStep(
        icon = Icons.Default.Language,
        title = "Choose Languages",
        description = "Select the source language (or Auto-detect) and the target language you want to translate to."
    ),
    GuideStep(
        icon = Icons.Default.Download,
        title = "Download Model",
        description = "Download the translation model (~30MB, one-time). This enables offline translation with zero data costs."
    ),
    GuideStep(
        icon = Icons.Default.Translate,
        title = "Tap START",
        description = "Tap the START button and grant the required permissions. A floating bubble will appear on your screen."
    ),
    GuideStep(
        icon = Icons.Default.Screenshot,
        title = "Switch to Any App",
        description = "Open any app — a game, manga, chat, website — anything with text you want to translate."
    ),
    GuideStep(
        icon = Icons.Default.TouchApp,
        title = "Tap the Bubble",
        description = "Tap the floating bubble to capture the screen. The text will be detected, translated, and shown as an overlay. Tap anywhere to dismiss."
    ),
)

@Composable
fun GuideDialog(onDismiss: () -> Unit) {
    var currentStep by remember { mutableIntStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DarkCard,
            shadowElevation = 8.dp,
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Title
                Text(
                    text = "How to Use",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Step content
                AnimatedContent(
                    targetState = currentStep,
                    label = "guideStep"
                ) { step ->
                    val guideStep = guideSteps[step]
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        // Icon circle
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(AccentTeal.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                guideStep.icon,
                                contentDescription = null,
                                tint = AccentTeal,
                                modifier = Modifier.size(32.dp),
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Step number
                        Text(
                            text = "Step ${step + 1} of ${guideSteps.size}",
                            fontSize = 12.sp,
                            color = AccentTeal,
                            fontWeight = FontWeight.Medium,
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Step title
                        Text(
                            text = guideStep.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Step description
                        Text(
                            text = guideStep.description,
                            fontSize = 14.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Progress dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    for (i in guideSteps.indices) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (i == currentStep) AccentTeal
                                    else TextMuted.copy(alpha = 0.3f)
                                )
                        )
                        if (i < guideSteps.size - 1) Spacer(modifier = Modifier.width(6.dp))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Skip", color = TextMuted)
                    }

                    Button(
                        onClick = {
                            if (currentStep < guideSteps.size - 1) {
                                currentStep++
                            } else {
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentTeal),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(
                            text = if (currentStep < guideSteps.size - 1) "Next" else "Get Started",
                            color = DarkBackground,
                            fontWeight = FontWeight.Medium,
                        )
                        if (currentStep < guideSteps.size - 1) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = DarkBackground,
                                modifier = Modifier.size(18.dp),
                            )
                        } else {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = DarkBackground,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
