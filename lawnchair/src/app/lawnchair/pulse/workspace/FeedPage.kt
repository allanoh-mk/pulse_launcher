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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.lawnchair.pulse.notifications.PulseNotification
import app.lawnchair.pulse.notifications.PulseNotifications
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
    val notifications by remember { PulseNotifications.observe(context) }
        .collectAsState(initial = emptyList())

    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
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
        item(key = "hero") { GreetingHeroCard(nowMillis) }
        if (notifications.isEmpty()) {
            item(key = "empty") { EmptyFeedCard() }
        } else {
            items(notifications, key = { it.key }) { notification ->
                NotificationCard(notification)
            }
        }
    }
}

@Composable
private fun GreetingHeroCard(nowMillis: Long) {
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
        Text(
            text = "$greeting · ${dateFormat.format(calendar.time)}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Composable
private fun NotificationCard(notification: PulseNotification) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
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
