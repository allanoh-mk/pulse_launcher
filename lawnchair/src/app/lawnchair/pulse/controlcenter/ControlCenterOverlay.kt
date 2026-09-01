package app.lawnchair.pulse.controlcenter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.android.launcher3.R

@Composable
fun ControlCenterOverlay(
    viewModel: ControlCenterViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    AnimatedVisibility(
        visible = state.isVisible,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it }),
        modifier = modifier.fillMaxSize()
    ) {
        val isLowEnd = !app.lawnchair.pulse.core.DeviceCapabilities.current(LocalContext.current).supportsRealtimeBlur
        val overlayBg = if (isLowEnd) Color.Black else Color.Black.copy(alpha = 0.4f)
        val panelBg = if (isLowEnd) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlayBg)
                .clickable { viewModel.setVisible(false) }
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(panelBg)
                    .clickable(enabled = false) {}
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ToggleButton(
                        label = "Wi-Fi",
                        isActive = state.isWifiEnabled,
                        iconRes = R.drawable.ic_setting, // Placeholder
                        onClick = { viewModel.toggleWifi() }
                    )
                    ToggleButton(
                        label = "Bluetooth",
                        isActive = state.isBluetoothEnabled,
                        iconRes = R.drawable.ic_setting, // Placeholder
                        onClick = { viewModel.toggleBluetooth() }
                    )
                    ToggleButton(
                        label = "DND",
                        isActive = state.isDndEnabled,
                        iconRes = R.drawable.ic_setting, // Placeholder
                        onClick = { viewModel.toggleDnd() }
                    )
                }

                SliderControl(
                    label = "Brightness",
                    value = state.brightness,
                    onValueChange = { viewModel.setBrightness(it) }
                )

                SliderControl(
                    label = "Volume",
                    value = state.volume,
                    onValueChange = { viewModel.setVolume(it) }
                )
            }
        }
    }
}

@Composable
private fun ToggleButton(
    label: String,
    isActive: Boolean,
    iconRes: Int,
    onClick: () -> Unit
) {
    val bgColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(bgColor)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun SliderControl(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
