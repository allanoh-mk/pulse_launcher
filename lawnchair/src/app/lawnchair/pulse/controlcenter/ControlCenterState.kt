package app.lawnchair.pulse.controlcenter

data class ControlCenterState(
    val isVisible: Boolean = false,
    val brightness: Float = 0.5f,
    val volume: Float = 0.5f,
    val isWifiEnabled: Boolean = true,
    val isDndEnabled: Boolean = false,
    val isBluetoothEnabled: Boolean = false
)
