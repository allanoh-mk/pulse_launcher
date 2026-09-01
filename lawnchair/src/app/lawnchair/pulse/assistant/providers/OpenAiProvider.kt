package app.lawnchair.pulse.assistant.providers

import app.lawnchair.pulse.assistant.SseReader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class OpenAiProvider : AiProvider {
    override val id = "openai"
    override val displayName = "OpenAI"
    override var apiKey: String = ""
    override var modelId: String = "gpt-4o"

    override fun streamChat(history: List<ChatMessage>): Flow<ChatStreamEvent> {
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

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $apiKey")
            .header("Accept", "text/event-stream")
            .post(requestBodyJson.toString().toRequestBody("application/json".toMediaType()))
            .build()

        return SseReader.stream(request).map { data ->
            if (data.startsWith("ERROR:")) {
                ChatStreamEvent.Error(data.removePrefix("ERROR:").trim())
            } else {
                try {
                    val json = JSONObject(data)
                    val choices = json.optJSONArray("choices")
                    if (choices != null && choices.length() > 0) {
                        val delta = choices.getJSONObject(0).optJSONObject("delta")
                        val content = delta?.optString("content")
                        if (content != null && content.isNotEmpty()) {
                            ChatStreamEvent.Token(content)
                        } else {
                            ChatStreamEvent.Token("") // Empty token, won't change UI but valid
                        }
                    } else {
                        ChatStreamEvent.Token("")
                    }
                } catch (e: Exception) {
                    ChatStreamEvent.Error("Parse error: ${e.message}")
                }
            }
        }
    }
}
