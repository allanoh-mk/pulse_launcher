package app.lawnchair.pulse.controlcenter

import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.media.AudioManager
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lawnchair.pulse.controlcenter.bridge.SystemBridge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ControlCenterViewModel(private val context: Context) : ViewModel() {
    private val _state = MutableStateFlow(ControlCenterState())
    val state: StateFlow<ControlCenterState> = _state.asStateFlow()

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val systemBridge = SystemBridge(context)
    private val bluetoothAdapter: BluetoothAdapter? = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

    init {
        refreshState()
    }

    fun setVisible(visible: Boolean) {
        _state.update { it.copy(isVisible = visible) }
        if (visible) {
            refreshState()
        }
    }

    fun setBrightness(value: Float) {
        _state.update { it.copy(brightness = value) }
        try {
            if (Settings.System.canWrite(context)) {
                val brightnessInt = (value * 255).toInt().coerceIn(1, 255)
                Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightnessInt)
            }
        } catch (e: Exception) {
            // Permission not granted
        }
    }

    fun setVolume(value: Float) {
        _state.update { it.copy(volume = value) }
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val targetVolume = (value * maxVolume).toInt()
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolume, 0)
    }

    fun toggleWifi() {
        systemBridge.openWifiPanel()
    }

    fun toggleBluetooth() {
        val newState = !_state.value.isBluetoothEnabled
        _state.update { it.copy(isBluetoothEnabled = newState) }
        try {
            @Suppress("DEPRECATION")
            if (newState) bluetoothAdapter?.enable() else bluetoothAdapter?.disable()
        } catch (e: SecurityException) {
            // Missing BLUETOOTH_CONNECT permission on Android 12+
        }
    }

    fun toggleDnd() {
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            return
        }
        val newState = !_state.value.isDndEnabled
        _state.update { it.copy(isDndEnabled = newState) }
        val filter = if (newState) {
            NotificationManager.INTERRUPTION_FILTER_PRIORITY
        } else {
            NotificationManager.INTERRUPTION_FILTER_ALL
        }
        notificationManager.setInterruptionFilter(filter)
    }

    fun toggleFlashlight() {
        val newState = !_state.value.isFlashlightOn
        _state.update { it.copy(isFlashlightOn = newState) }
        systemBridge.setFlashlight(newState)
    }

    fun toggleAutoRotate() {
        val newState = systemBridge.toggleAutoRotate()
        _state.update { it.copy(isAutoRotateEnabled = newState) }
    }

    private fun refreshState() {
        viewModelScope.launch {
            val brightness = try {
                Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS) / 255f
            } catch (e: Settings.SettingNotFoundException) {
                0.5f
            }

            val currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val volume = if (maxVol > 0) currentVol.toFloat() / maxVol else 0f

            val isWifiEnabled = try {
                Settings.Global.getInt(context.contentResolver, Settings.Global.WIFI_ON) == 1
            } catch (e: Exception) {
                false
            }

            val isBluetoothEnabled = bluetoothAdapter?.isEnabled == true
            val isDndEnabled = notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL
            val isFlashlightOn = _state.value.isFlashlightOn
            val isAutoRotateEnabled = systemBridge.isAutoRotateEnabled()

            _state.update {
                it.copy(
                    brightness = brightness,
                    volume = volume,
                    isWifiEnabled = isWifiEnabled,
                    isBluetoothEnabled = isBluetoothEnabled,
                    isDndEnabled = isDndEnabled,
                    isFlashlightOn = isFlashlightOn,
                    isAutoRotateEnabled = isAutoRotateEnabled,
                )
            }
        }
    }
}
