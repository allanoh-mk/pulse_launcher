package app.lawnchair.pulse.assistant

import app.lawnchair.pulse.assistant.providers.ChatMessage
import app.lawnchair.pulse.assistant.providers.NineRouterProvider
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NineRouterProviderTest {

    private val provider = NineRouterProvider()

    @Test
    fun `default provider config contains 9router credentials`() {
        assertEquals("9router", provider.id)
        assertEquals("9router", provider.displayName)
        assertEquals(NineRouterProvider.DEFAULT_API_KEY, provider.apiKey)
        assertEquals(NineRouterProvider.MODEL_GEMINI_FLASH, provider.modelId)
        assertEquals(NineRouterProvider.DEFAULT_PRIMARY_URL, provider.primaryBaseUrl)
        assertEquals(NineRouterProvider.DEFAULT_FAILOVER_URL, provider.failoverBaseUrl)
    }

    @Test
    fun `request body serializes history with streaming flag`() {
        val history = listOf(
            ChatMessage(ChatMessage.Role.SYSTEM, "You are Pulse Assistant"),
            ChatMessage(ChatMessage.Role.USER, "Play some jazz"),
        )
        val jsonString = provider.buildRequestBody(history)
        val json = JSONObject(jsonString)

        assertEquals(provider.modelId, json.getString("model"))
        assertTrue(json.getBoolean("stream"))

        val messages = json.getJSONArray("messages")
        assertEquals(2, messages.length())
        assertEquals("system", messages.getJSONObject(0).getString("role"))
        assertEquals("You are Pulse Assistant", messages.getJSONObject(0).getString("content"))
        assertEquals("user", messages.getJSONObject(1).getString("role"))
        assertEquals("Play some jazz", messages.getJSONObject(1).getString("content"))
    }

    @Test
    fun `request builder formats completions URL and auth header`() {
        val request = provider.buildRequest("http://localhost:20128/v1", "{}")
        assertEquals("http://localhost:20128/v1/chat/completions", request.url.toString())
        assertEquals("Bearer ${NineRouterProvider.DEFAULT_API_KEY}", request.header("Authorization"))
        assertEquals("text/event-stream", request.header("Accept"))
    }

    @Test
    fun `parse delta token extracts openai compatible content`() {
        val rawData = """{"id":"chatcmpl-123","choices":[{"index":0,"delta":{"content":"Hello from 9router!"}}]}"""
        val token = provider.parseDeltaToken(rawData)
        assertEquals("Hello from 9router!", token)
    }

    @Test
    fun `preset models list contains required high performance models`() {
        assertTrue(NineRouterProvider.PRESET_MODELS.contains(NineRouterProvider.MODEL_GEMINI_FLASH))
        assertTrue(NineRouterProvider.PRESET_MODELS.contains(NineRouterProvider.MODEL_CLAUDE_SONNET))
        assertTrue(NineRouterProvider.PRESET_MODELS.contains(NineRouterProvider.MODEL_GROQ_LLAMA))
    }
}
