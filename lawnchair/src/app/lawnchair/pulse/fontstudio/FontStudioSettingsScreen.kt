package app.lawnchair.pulse.fontstudio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.lawnchair.preferences.getAdapter
import app.lawnchair.pulse.core.pulsePreferences

/**
 * Font Studio: pick a family, preview it at Regular/Medium/Bold immediately,
 * persisted via [app.lawnchair.pulse.core.PulsePreferences.fontOption].
 */
@Composable
fun FontStudioSettingsScreen() {
    val preferences = pulsePreferences()
    val adapter = preferences.fontOption.getAdapter()
    val selected = PulseFontOption.fromName(adapter.state.value)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = "Font Studio", style = MaterialTheme.typography.headlineSmall)

        PulseFontOption.entries.forEach { option ->
            val isSelected = option == selected
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    )
                    .clickable { adapter.onChange(option.name) }
                    .padding(16.dp),
            ) {
                Text(text = option.displayName, fontFamily = option.fontFamily, fontWeight = FontWeight.Medium)
                Text(
                    text = "The quick brown fox jumps",
                    fontFamily = option.fontFamily,
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "Bold pairing preview",
                    fontFamily = option.fontFamily,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
