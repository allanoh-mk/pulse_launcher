package app.lawnchair.pulse.data.repositories

import android.content.Context
import android.provider.CalendarContract
import java.util.Calendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for calendar events using CalendarContract.Instances.
 * Returns events for the next 24 hours.
 */
class CalendarRepository(private val context: Context) {

    data class CalendarEvent(
        val title: String,
        val startTime: Long,
        val endTime: Long,
        val location: String?,
    )

    suspend fun getNextEvents(): List<CalendarEvent> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val tomorrow = Calendar.getInstance().apply {
            timeInMillis = now
            add(Calendar.DAY_OF_YEAR, 1)
        }.timeInMillis

        val projection = arrayOf(
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END,
            CalendarContract.Instances.EVENT_LOCATION,
        )

        val selection = "${CalendarContract.Instances.BEGIN} >= ? AND ${CalendarContract.Instances.END} <= ?"
        val selectionArgs = arrayOf(now.toString(), tomorrow.toString())

        val sortOrder = "${CalendarContract.Instances.BEGIN} ASC"

        val cursor = context.contentResolver.query(
            CalendarContract.Instances.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder,
        )

        val events = mutableListOf<CalendarEvent>()
        cursor?.use {
            while (it.moveToNext()) {
                val title = it.getString(0)
                val startTime = it.getLong(1)
                val endTime = it.getLong(2)
                val location = it.getString(3)

                events.add(CalendarEvent(title, startTime, endTime, location))
            }
        }

        events
    }
}
