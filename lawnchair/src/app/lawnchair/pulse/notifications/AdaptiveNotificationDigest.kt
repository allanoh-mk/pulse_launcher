package app.lawnchair.pulse.notifications

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.digestWeightsStore by preferencesDataStore(name = "digest_weights")

/**
 * An extractive summarization engine that runs locally with minimal RAM (O(1) memory footprint).
 * Uses a TF-IDF inspired approach with adaptive user weights instead of a massive neural LLM.
 */
object AdaptiveNotificationDigest {
    private val weightsKey = stringPreferencesKey("weights_map")
    private var localWeights = mutableMapOf<String, Float>()
    private var initialized = false

    suspend fun initialize(context: Context) {
        if (initialized) return
        val prefs = context.digestWeightsStore.data.first()
        val json = prefs[weightsKey]
        if (json != null) {
            try {
                localWeights = Json.decodeFromString(json)
            } catch (e: Exception) {
                // Ignore
            }
        }
        initialized = true
    }

    suspend fun recordInteraction(context: Context, text: String, isTap: Boolean) {
        val words = tokenize(text)
        val delta = if (isTap) 0.5f else -0.2f
        
        words.forEach { word ->
            localWeights[word] = (localWeights[word] ?: 1.0f) + delta
            // Floor at 0.1f
            if (localWeights[word]!! < 0.1f) {
                localWeights[word] = 0.1f
            }
        }
        
        context.digestWeightsStore.edit {
            it[weightsKey] = Json.encodeToString(localWeights)
        }
    }

    fun buildDigest(notifications: List<PulseNotification>): List<String> {
        if (notifications.isEmpty()) return emptyList()

        // 1. Score notifications
        val scored = notifications.map { notif ->
            val text = "${notif.title.orEmpty()} ${notif.text.orEmpty()}"
            val words = tokenize(text)
            
            var score = 0f
            words.forEach { word ->
                score += (localWeights[word] ?: 1.0f)
            }
            
            // Recency bias (newer is better)
            val ageHours = (System.currentTimeMillis() - notif.postTimeMillis) / 3600000f
            score = score / (1f + ageHours)
            
            Pair(notif, score)
        }

        // 2. Take top 3
        val top = scored.sortedByDescending { it.second }.take(3).map { it.first }
        
        // 3. Extract key sentence
        return top.map { notif ->
            val fullText = notif.text.orEmpty()
            val firstSentence = fullText.split(Regex("[.!?]")).firstOrNull()?.trim() ?: fullText
            val summaryText = if (firstSentence.length > 60) firstSentence.substring(0, 57) + "..." else firstSentence
            "• ${notif.title}: $summaryText"
        }
    }

    private fun tokenize(text: String): List<String> {
        // Simple tokenization: lowercase, alphanumeric > 3 chars
        return text.lowercase()
            .split(Regex("\\W+"))
            .filter { it.length > 3 && it !in stopWords }
    }

    private val stopWords = setOf("this", "that", "with", "from", "your", "have", "they", "will", "would", "there", "their")
}
