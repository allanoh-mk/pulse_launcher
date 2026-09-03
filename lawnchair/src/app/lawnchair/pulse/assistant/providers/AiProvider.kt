package app.lawnchair.pulse.assistant.providers

import kotlinx.coroutines.flow.Flow

data class ChatMessage(val role: Role, val content: String) {
    enum class Role { USER, ASSISTANT, SYSTEM }
}

sealed class ChatStreamEvent {
    data class Token(val text: String) : ChatStreamEvent()
    data class Error(val message: String) : ChatStreamEvent()
    object Done : ChatStreamEvent()
}

interface AiProvider {
    val id: String
    val displayName: String
    var apiKey: String // Will be stored encrypted via KeystoreHelper
    var modelId: String

    fun streamChat(history: List<ChatMessage>): Flow<ChatStreamEvent>
}
