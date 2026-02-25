package com.autotranslate.app.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.autotranslate.app.db.TranslationHistoryEntity
import com.autotranslate.app.ui.theme.AccentTeal
import com.autotranslate.app.ui.theme.DarkCard
import com.autotranslate.app.ui.theme.TextMuted
import com.autotranslate.app.ui.theme.TextPrimary
import com.autotranslate.app.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryItem(
    item: TranslationHistoryEntity,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.US) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .clickable { expanded = !expanded }
            .padding(16.dp)
            .animateContentSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = item.sourceLangCode.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = AccentTeal,
            )
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier
                    .size(14.dp)
                    .padding(horizontal = 2.dp),
            )
            Text(
                text = item.targetLangCode.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = AccentTeal,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = dateFormat.format(Date(item.timestamp)),
                style = MaterialTheme.typography.labelMedium,
                color = TextMuted,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = item.originalText,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            maxLines = if (expanded) Int.MAX_VALUE else 2,
            overflow = TextOverflow.Ellipsis,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = item.translatedText,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary,
            maxLines = if (expanded) Int.MAX_VALUE else 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
