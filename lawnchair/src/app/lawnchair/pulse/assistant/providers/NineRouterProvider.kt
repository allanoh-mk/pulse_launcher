package app.lawnchair.pulse.assistant.providers

import app.lawnchair.pulse.assistant.SseReader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

/**
 * 9router AI Provider with automatic endpoint failover.
 *
 * Connects to localhost:20128/v1 when local router daemon is running, and
 * automatically fails over to the remote tunnel (https://rgs8m7t.abc-tunnel.us/v1)
 * when off-device or when the local port is unreachable.
 */
class NineRouterProvider(
    var primaryBaseUrl: String = DEFAULT_PRIMARY_URL,
    var failoverBaseUrl: String = DEFAULT_FAILOVER_URL,
) : AiProvider {

    override val id: String = PROVIDER_ID
    override val displayName: String = "9router"
    override var apiKey: String = DEFAULT_API_KEY
    override var modelId: String = MODEL_GEMINI_FLASH

    companion object {
        const val PROVIDER_ID = "9router"
        const val DEFAULT_PRIMARY_URL = "http://localhost:20128/v1"
        const val DEFAULT_FAILOVER_URL = "https://rgs8m7t.abc-tunnel.us/v1"
        const val DEFAULT_API_KEY = "sk-e96983da464641d0-wpk6zo-a854b091"

        // Recommended preset models available through 9router
        const val MODEL_GEMINI_FLASH = "af/google/gemini-2.5-flash"
        const val MODEL_CLAUDE_SONNET = "af/anthropic/claude-3.7-sonnet"
        const val MODEL_GROQ_LLAMA = "groq/llama-3.3-70b-versatile"
        const val MODEL_MINIMAX = "minimax/MiniMax-M3"

        val PRESET_MODELS = listOf(
            MODEL_GEMINI_FLASH,
            MODEL_CLAUDE_SONNET,
            MODEL_GROQ_LLAMA,
            MODEL_MINIMAX,
        )
    }

    override fun streamChat(history: List<ChatMessage>): Flow<ChatStreamEvent> = flow {
        val requestBody = buildRequestBody(history)

        // Try primary endpoint first
        val primaryRequest = buildRequest(primaryBaseUrl, requestBody)
        var primaryFailed = false
        var receivedAnyToken = false

        try {
            SseReader.stream(primaryRequest).collect { chunk ->
                if (chunk.startsWith("ERROR:")) {
                    primaryFailed = true
                    return@collect
                }
                val token = parseDeltaToken(chunk)
                if (token != null) {
                    receivedAnyToken = true
                    emit(ChatStreamEvent.Token(token))
                }
            }
        } catch (e: Exception) {
            primaryFailed = true
        }

        // If primary failed and we haven't emitted tokens yet, attempt failover endpoint
        if (primaryFailed && !receivedAnyToken) {
            try {
                val failoverRequest = buildRequest(failoverBaseUrl, requestBody)
                SseReader.stream(failoverRequest).collect { chunk ->
                    if (chunk.startsWith("ERROR:")) {
                        emit(ChatStreamEvent.Error(chunk.removePrefix("ERROR:").trim()))
                    } else {
                        val token = parseDeltaToken(chunk)
                        if (token != null) {
                            emit(ChatStreamEvent.Token(token))
                        }
                    }
                }
            } catch (e: Exception) {
                emit(ChatStreamEvent.Error("9router connection failed: ${e.message}"))
            }
        }

        emit(ChatStreamEvent.Done)
    }

    fun buildRequestBody(history: List<ChatMessage>): String {
        val messagesArray = JSONArray()
        history.forEach { msg ->
            val obj = JSONObject()
            obj.put("role", msg.role.name.lowercase())
            obj.put("content", msg.content)
            messagesArray.put(obj)
        }

        val requestBodyJson = JSONObject().apply {
            put("model", modelId)
            put("messages", messagesArray)
            put("stream", true)
        }
        return requestBodyJson.toString()
    }

    fun buildRequest(baseUrl: String, body: String): Request {
        val normalizedBase = baseUrl.trimEnd('/')
        val url = "$normalizedBase/chat/completions"
        return Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $apiKey")
            .header("Accept", "text/event-stream")
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()
    }

    fun parseDeltaToken(rawSseData: String): String? {
        return try {
            val json = JSONObject(rawSseData)
            val choices = json.optJSONArray("choices")
            if (choices != null && choices.length() > 0) {
                val delta = choices.getJSONObject(0).optJSONObject("delta")
                delta?.optString("content")
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
