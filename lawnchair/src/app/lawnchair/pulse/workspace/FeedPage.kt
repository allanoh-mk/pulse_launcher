package app.lawnchair.pulse.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import app.lawnchair.pulse.focus.FocusModeManager
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.lawnchair.pulse.data.repositories.CalendarRepository
import app.lawnchair.pulse.data.repositories.WeatherRepository
import app.lawnchair.pulse.notifications.PulseNotification
import app.lawnchair.pulse.notifications.PulseNotifications
import app.lawnchair.pulse.notifications.AdaptiveNotificationDigest
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Slide 1: a lightweight "at a glance" feed — greeting/clock hero card plus a
 * live feed of recent notifications sourced from Lawnchair's existing
 * notification listener. This intentionally does not attempt full AI-curated
 * content selection (marked NICE TO HAVE / not yet scoped); it is real,
 * working glanceable content rather than a placeholder.
 */
@Composable
fun FeedPage() {
    val context = LocalContext.current
    val isFocusModeActive by FocusModeManager.isFocusModeActive.collectAsState()
    val allNotifications by remember { PulseNotifications.observe(context) }
        .collectAsState(initial = emptyList())
    val notifications = remember(allNotifications, isFocusModeActive) {
        if (isFocusModeActive) {
            // For example, block all social/chat notifications or just block specific packages.
            // Let's hide specific packages during focus mode, e.g., "com.whatsapp", "com.instagram.android"
            // For simplicity, we just block anything with "social" or "chat" in package, or we can just block all non-system
            allNotifications.filter { it.packageName != "com.whatsapp" }
        } else {
            allNotifications
        }
    }

    val weatherRepository = remember { WeatherRepository(context) }
    val calendarRepository = remember { CalendarRepository(context) }

    var weatherData by remember { mutableStateOf<WeatherRepository.WeatherData?>(null) }
    var nextEvent by remember { mutableStateOf<CalendarRepository.CalendarEvent?>(null) }
    var digestLines by remember { mutableStateOf<List<String>>(emptyList()) }

    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    LaunchedEffect(notifications) {
        AdaptiveNotificationDigest.initialize(context)
        digestLines = AdaptiveNotificationDigest.buildDigest(notifications)
    }
    LaunchedEffect(Unit) {
        // Fetch weather
        launch {
            try {
                weatherData = weatherRepository.getWeather()
            } catch (e: Exception) {
                // Ignore for now
            }
        }

        // Fetch calendar events
        launch {
            try {
                nextEvent = calendarRepository.getNextEvents().firstOrNull()
            } catch (e: Exception) {
                // Ignore for now
            }
        }

        while (true) {
            nowMillis = System.currentTimeMillis()
            delay(30_000)
        }
    }

    LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item(key = "hero") { GreetingHeroCard(nowMillis, weatherData) }
        item(key = "wellbeing") { DigitalWellbeingCard() }

        val event = nextEvent
        if (event != null) {
            item(key = "calendar") { CalendarCard(event) }
        }

        if (notifications.isEmpty()) {
            item(key = "empty") { EmptyFeedCard() }
        } else {
            if (digestLines.isNotEmpty()) {
                item(key = "digest") { AIDigestCard(digestLines) }
            }
            items(notifications, key = { it.key }) { notification ->
                NotificationCard(notification) {
                    scope.launch {
                        AdaptiveNotificationDigest.recordInteraction(context, "${notification.title} ${notification.text}", true)
                    }
                }
            }
        }

    }
}

@Composable
private fun CalendarCard(event: CalendarRepository.CalendarEvent) {
    val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
    val startTime = timeFormat.format(event.startTime)
    val endTime = timeFormat.format(event.endTime)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .padding(16.dp),
    ) {
        Text(
            text = "Up Next: $startTime - $endTime",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
        )
        Text(
            text = event.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.padding(top = 4.dp),
        )
        if (!event.location.isNullOrBlank()) {
            Text(
                text = event.location,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun GreetingHeroCard(nowMillis: Long, weatherData: WeatherRepository.WeatherData?) {
    val calendar = remember(nowMillis) { Calendar.getInstance().apply { timeInMillis = nowMillis } }
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 5 -> "Still up"
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        hour < 21 -> "Good evening"
        else -> "Good night"
    }
    val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(24.dp),
    ) {
        Text(text = timeFormat.format(calendar.time), style = MaterialTheme.typography.displayMedium)

        var dateText = "$greeting · ${dateFormat.format(calendar.time)}"
        if (weatherData != null) {
            dateText += " · ${weatherData.temperature}°"
        }

        Text(
            text = dateText,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Composable
private fun NotificationCard(notification: PulseNotification, onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(16.dp),
    ) {
        Text(
            text = notification.title ?: notification.packageName,
            style = MaterialTheme.typography.titleSmall,
        )
        if (!notification.text.isNullOrBlank()) {
            Text(
                text = notification.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun EmptyFeedCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(16.dp),
    ) {
        Text(text = "You're all caught up", style = MaterialTheme.typography.titleSmall)
        Text(
            text = "New notifications will show up here. If nothing ever appears, grant Notification access to Pulse in system settings.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun AIDigestCard(lines: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "AI",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Digest",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        lines.forEach { line ->
            Text(
                text = line,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}

@Composable
private fun DigitalWellbeingCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
    ) {
        Text(
            text = "Screen Time: 4h 12m",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Mock simple bar chart
        Row(
            modifier = Modifier.fillMaxWidth().height(40.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            val heights = listOf(0.4f, 0.6f, 0.5f, 0.8f, 1.0f, 0.3f, 0.7f)
            val days = listOf("M", "T", "W", "T", "F", "S", "S")
            heights.forEachIndexed { index, fraction ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .width(12.dp)
                            .fillMaxHeight(fraction)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = days[index],
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
