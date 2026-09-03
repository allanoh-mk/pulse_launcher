package app.lawnchair.pulse.search

import android.content.Context
import android.content.pm.LauncherApps
import android.database.Cursor
import android.net.Uri
import android.os.Process
import android.provider.ContactsContract
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchDispatcher(private val context: Context) {

    private val launcherApps = runCatching {
        context.getSystemService(LauncherApps::class.java)
    }.getOrNull()

    suspend fun search(query: String): List<SearchResult> = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return@withContext emptyList()

        val results = mutableListOf<SearchResult>()

        // 1. Offline Math Evaluation
        if (MathEvaluator.isMathExpression(trimmed)) {
            val calcResult = MathEvaluator.evaluate(trimmed)
            if (calcResult != null) {
                results.add(
                    SearchResult(
                        id = "calc_$trimmed",
                        title = calcResult,
                        subtitle = "Calculation = $trimmed",
                        type = ResultType.CALCULATION,
                        actionData = calcResult,
                    ),
                )
            }
        }

        // 2. Installed Apps Search
        results.addAll(searchApps(trimmed))

        // 3. Contacts Search
        results.addAll(searchContacts(trimmed))

        // 4. Media Files Search
        results.addAll(searchFiles(trimmed))

        // 5. Web Search Providers
        results.add(
            SearchResult(
                id = "web_google_$trimmed",
                title = "Search Google for \"$trimmed\"",
                subtitle = "Google Search",
                type = ResultType.WEB,
                actionData = "https://www.google.com/search?q=${Uri.encode(trimmed)}",
            ),
        )
        results.add(
            SearchResult(
                id = "web_ddg_$trimmed",
                title = "Search DuckDuckGo for \"$trimmed\"",
                subtitle = "Privacy Search",
                type = ResultType.WEB,
                actionData = "https://duckduckgo.com/?q=${Uri.encode(trimmed)}",
            ),
        )

        results
    }

    fun searchApps(query: String): List<SearchResult> {
        val apps = launcherApps ?: return emptyList()
        val user = Process.myUserHandle()
        val activities = runCatching { apps.getActivityList(null, user) }.getOrDefault(emptyList())

        return activities.filter {
            it.label.toString().contains(query, ignoreCase = true)
        }.take(5).map {
            SearchResult(
                id = it.applicationInfo.packageName,
                title = it.label.toString(),
                subtitle = it.applicationInfo.packageName,
                type = ResultType.APP,
                actionData = it.componentName.flattenToString(),
            )
        }
    }

    fun searchContacts(query: String): List<SearchResult> {
        val list = mutableListOf<SearchResult>()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
        )
        val selection = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$query%")

        try {
            val cursor: Cursor? = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC LIMIT 3",
            )
            cursor?.use {
                val nameCol = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberCol = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val idCol = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)

                while (it.moveToNext()) {
                    val name = if (nameCol >= 0) it.getString(nameCol) else "Contact"
                    val number = if (numberCol >= 0) it.getString(numberCol) else ""
                    val id = if (idCol >= 0) it.getString(idCol) else name
                    list.add(
                        SearchResult(
                            id = "contact_$id",
                            title = name,
                            subtitle = number,
                            type = ResultType.CONTACT,
                            actionData = "tel:$number",
                        ),
                    )
                }
            }
        } catch (_: Exception) {}
        return list
    }

    fun searchFiles(query: String): List<SearchResult> {
        val list = mutableListOf<SearchResult>()
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.SIZE,
        )
        val selection = "${MediaStore.Files.FileColumns.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$query%")

        try {
            val cursor: Cursor? = context.contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                selectionArgs,
                "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC LIMIT 3",
            )
            cursor?.use {
                val nameCol = it.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
                val idCol = it.getColumnIndex(MediaStore.Files.FileColumns._ID)

                while (it.moveToNext()) {
                    val name = if (nameCol >= 0) it.getString(nameCol) else "File"
                    val id = if (idCol >= 0) it.getString(idCol) else name
                    list.add(
                        SearchResult(
                            id = "file_$id",
                            title = name,
                            subtitle = "Media File",
                            type = ResultType.FILE,
                        ),
                    )
                }
            }
        } catch (_: Exception) {}
        return list
    }
}
