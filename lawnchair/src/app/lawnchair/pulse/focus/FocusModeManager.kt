package app.lawnchair.pulse.focus

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

object FocusModeManager {
    private val _isFocusModeActive = MutableStateFlow(false)
    val isFocusModeActive: StateFlow<Boolean> = _isFocusModeActive.asStateFlow()

    // Example schedule: 9 AM to 5 PM
    private var startHour = 9
    private var endHour = 17
    
    // Which tiles to hide during focus mode
    val hiddenTilesInFocusMode = setOf("media", "weather")

    fun updateFocusModeState() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        _isFocusModeActive.value = hour in startHour until endHour
    }

    fun setSchedule(start: Int, end: Int) {
        startHour = start
        endHour = end
        updateFocusModeState()
    }
}
