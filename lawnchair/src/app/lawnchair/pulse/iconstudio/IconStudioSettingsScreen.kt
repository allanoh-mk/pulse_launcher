package app.lawnchair.pulse.iconstudio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.lawnchair.pulse.data.db.GLOBAL_ICON_STYLE_KEY
import app.lawnchair.pulse.data.db.IconStyleConfig
import app.lawnchair.pulse.data.db.PulseDatabase
import kotlinx.coroutines.launch

/**
 * Global Icon Studio settings: pick a shape + style, see it applied live to
 * the launcher's own icon, then persist as the default for every app that
 * doesn't have a per-app override in [app.lawnchair.pulse.data.db.IconStyleConfigDao].
 */
@Composable
fun IconStudioSettingsScreen() {
    val context = LocalContext.current
    val dao = remember { PulseDatabase.INSTANCE.get(context).iconStyleConfigDao() }
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    var shape by remember { mutableStateOf(IconShapePreset.SQUIRCLE) }
    var style by remember { mutableStateOf(IconStyle.LIQUID_GLASS) }
    var sizeScale by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(Unit) {
        dao.get(GLOBAL_ICON_STYLE_KEY)?.let { existing ->
            shape = runCatching { IconShapePreset.valueOf(existing.shape) }.getOrDefault(IconShapePreset.SQUIRCLE)
            style = runCatching { IconStyle.valueOf(existing.style) }.getOrDefault(IconStyle.LIQUID_GLASS)
            sizeScale = existing.sizeScale
        }
    }

    val appIcon = remember { context.packageManager.getApplicationIcon(context.packageName) }
    val previewConfig = remember(shape, style, sizeScale) { IconRenderConfig(shape, style, sizeScale) }
    val previewBitmap = remember(previewConfig) {
        IconRenderer.render(context, context.packageName, appIcon, previewConfig)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(text = "Icon Studio", style = MaterialTheme.typography.headlineSmall)

        Image(
            bitmap = previewBitmap.asImageBitmap(),
            contentDescription = "Icon preview",
            modifier = Modifier
                .size(96.dp)
                .align(Alignment.CenterHorizontally),
        )

        Text(text = "Shape", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconShapePreset.entries.forEach { preset ->
                SelectableChip(
                    label = preset.name.lowercase().replaceFirstChar { it.uppercase() },
                    selected = preset == shape,
                    onClick = { shape = preset },
                )
            }
        }

        Text(text = "Style", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconStyle.entries.forEach { entry ->
                SelectableChip(
                    label = entry.name.lowercase().replaceFirstChar { it.uppercase() },
                    selected = entry == style,
                    onClick = { style = entry },
                )
            }
        }

        Text(text = "Size", style = MaterialTheme.typography.titleSmall)
        Slider(
            value = sizeScale,
            onValueChange = { sizeScale = it },
            valueRange = 0.7f..1f,
            onValueChangeFinished = {
                scope.launch {
                    IconRenderer.clearCache()
                    dao.upsert(
                        IconStyleConfig(
                            packageName = GLOBAL_ICON_STYLE_KEY,
                            shape = shape.name,
                            style = style.name,
                            sizeScale = sizeScale,
                        ),
                    )
                }
            },
        )
    }

    LaunchedEffect(shape, style) {
        IconRenderer.clearCache()
        dao.upsert(
            IconStyleConfig(
                packageName = GLOBAL_ICON_STYLE_KEY,
                shape = shape.name,
                style = style.name,
                sizeScale = sizeScale,
            ),
        )
    }
}

@Composable
private fun SelectableChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val background = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(text = label, color = contentColor, style = MaterialTheme.typography.labelLarge)
    }
}
