package com.autotranslate.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.autotranslate.app.data.PrefsManager
import com.autotranslate.app.ui.theme.AccentRed
import com.autotranslate.app.ui.theme.AccentTeal
import com.autotranslate.app.ui.theme.DarkBackground
import com.autotranslate.app.ui.theme.DarkCard
import com.autotranslate.app.ui.theme.DarkSurface
import com.autotranslate.app.ui.theme.TextMuted
import com.autotranslate.app.ui.theme.TextPrimary
import com.autotranslate.app.ui.theme.TextSecondary
import com.autotranslate.app.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
) {
    var autoDetect by remember { mutableStateOf(PrefsManager.autoDetect) }
    var overlayOpacity by remember { mutableFloatStateOf(PrefsManager.overlayOpacity) }
    var textSize by remember { mutableIntStateOf(PrefsManager.textSize) }
    var showClearDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        TopAppBar(
            title = { Text("Settings", style = MaterialTheme.typography.titleLarge) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = DarkBackground,
                titleContentColor = TextPrimary,
            ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            // Translation section
            SectionHeader("Translation")

            SettingsToggle(
                icon = Icons.Default.AutoFixHigh,
                title = "Auto-detect Source Language",
                subtitle = "Automatically detect the language on screen",
                checked = autoDetect,
                onCheckedChange = {
                    autoDetect = it
                    PrefsManager.autoDetect = it
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Overlay section
            SectionHeader("Overlay")

            SettingsSlider(
                icon = Icons.Default.Opacity,
                title = "Overlay Opacity",
                value = overlayOpacity,
                valueRange = 0.5f..1f,
                valueLabel = "${(overlayOpacity * 100).toInt()}%",
                onValueChange = {
                    overlayOpacity = it
                    PrefsManager.overlayOpacity = it
                },
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsChoice(
                icon = Icons.Default.FormatSize,
                title = "Text Size",
                options = listOf("Small", "Medium", "Large"),
                selectedIndex = textSize,
                onSelect = {
                    textSize = it
                    PrefsManager.textSize = it
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Data section
            SectionHeader("Data")

            SettingsButton(
                icon = Icons.Default.DeleteForever,
                title = "Clear Translation History",
                subtitle = "Remove all saved translations",
                color = AccentRed,
                onClick = { showClearDialog = true },
            )

            Spacer(modifier = Modifier.height(16.dp))

            // About section
            SectionHeader("About")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkCard)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = TextMuted)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Auto Screen Translate", color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                    Text("Version 1.0.0", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear History", color = TextPrimary) },
            text = { Text("Delete all translation history? This cannot be undone.", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearHistory()
                    showClearDialog = false
                }) {
                    Text("Clear", color = AccentRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurface,
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = AccentTeal,
        modifier = Modifier.padding(vertical = 8.dp),
    )
}

@Composable
private fun SettingsToggle(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = TextSecondary)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = AccentTeal,
                checkedTrackColor = AccentTeal.copy(alpha = 0.3f),
            ),
        )
    }
}

@Composable
private fun SettingsSlider(
    icon: ImageVector,
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    valueLabel: String,
    onValueChange: (Float) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = TextSecondary)
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, color = TextPrimary, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Text(valueLabel, color = AccentTeal, style = MaterialTheme.typography.labelLarge)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = AccentTeal,
                activeTrackColor = AccentTeal,
                inactiveTrackColor = DarkSurface,
            ),
        )
    }
}

@Composable
private fun SettingsChoice(
    icon: ImageVector,
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = TextSecondary)
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
        ) {
            options.forEachIndexed { index, option ->
                val isSelected = index == selectedIndex
                Text(
                    text = option,
                    color = if (isSelected) DarkBackground else TextSecondary,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) AccentTeal else DarkSurface)
                        .clickable { onSelect(index) }
                        .padding(vertical = 10.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun SettingsButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = color)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, color = color, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
