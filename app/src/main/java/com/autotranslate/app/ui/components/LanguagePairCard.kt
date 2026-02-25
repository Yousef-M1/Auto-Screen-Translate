package com.autotranslate.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.autotranslate.app.data.Language
import com.autotranslate.app.ui.theme.AccentTeal
import com.autotranslate.app.ui.theme.DarkCard
import com.autotranslate.app.ui.theme.DarkCardElevated
import com.autotranslate.app.ui.theme.TextPrimary
import com.autotranslate.app.ui.theme.TextSecondary

@Composable
fun LanguagePairCard(
    sourceLanguage: Language,
    targetLanguage: Language,
    onSourceClick: () -> Unit,
    onTargetClick: () -> Unit,
    onSwapClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCard)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Source language
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(DarkCardElevated)
                .clickable(onClick = onSourceClick)
                .padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "FROM",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
            )
            Text(
                text = sourceLanguage.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        // Swap button
        IconButton(
            onClick = onSwapClick,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(AccentTeal.copy(alpha = 0.15f)),
        ) {
            Icon(
                Icons.Default.SwapHoriz,
                contentDescription = "Swap languages",
                tint = AccentTeal,
            )
        }

        // Target language
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(DarkCardElevated)
                .clickable(onClick = onTargetClick)
                .padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "TO",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
            )
            Text(
                text = targetLanguage.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}
