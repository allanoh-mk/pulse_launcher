package app.lawnchair.pulse.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lawnchair.pulse.assistant.providers.AiProvider
import app.lawnchair.pulse.assistant.providers.ChatMessage
import app.lawnchair.pulse.assistant.providers.ChatStreamEvent
import app.lawnchair.pulse.assistant.providers.OpenAiProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import app.lawnchair.pulse.assistant.providers.LocalAiProvider

data class AssistantState(
    val messages: List<ChatMessage> = emptyList(),
    val isGenerating: Boolean = false,
    val currentInput: String = "",
    val error: String? = null
)

class AssistantViewModel : ViewModel() {
    private val _state = MutableStateFlow(AssistantState())
    val state: StateFlow<AssistantState> = _state.asStateFlow()

    private val provider: AiProvider = LocalAiProvider()
    private var chatJob: Job? = null

    fun setApiKey(key: String) {
        val encrypted = KeystoreHelper.encrypt(key)
        // In a real app, save 'encrypted' to SharedPreferences.
        provider.apiKey = key
    }

    fun onInputChange(input: String) {
        _state.update { it.copy(currentInput = input) }
    }

    fun sendMessage() {
        val userText = _state.value.currentInput.trim()
        if (userText.isEmpty()) return

        val userMessage = ChatMessage(ChatMessage.Role.USER, userText)
        _state.update { 
            it.copy(
                messages = it.messages + userMessage,
                currentInput = "",
                isGenerating = true,
                error = null
            )
        }

        // Add a placeholder for the assistant's reply
        val assistantMessageIndex = _state.value.messages.size
        _state.update { it.copy(messages = it.messages + ChatMessage(ChatMessage.Role.ASSISTANT, "")) }

        chatJob?.cancel()
        chatJob = viewModelScope.launch {
            val history = _state.value.messages.take(assistantMessageIndex) // All up to the user message
            provider.streamChat(history).collect { event ->
                when (event) {
                    is ChatStreamEvent.Token -> {
                        _state.update { currentState ->
                            val updatedMessages = currentState.messages.toMutableList()
                            val existing = updatedMessages[assistantMessageIndex]
                            updatedMessages[assistantMessageIndex] = existing.copy(content = existing.content + event.text)
                            currentState.copy(messages = updatedMessages)
                        }
                    }
                    is ChatStreamEvent.Error -> {
                        _state.update { it.copy(isGenerating = false, error = event.message) }
                    }
                    is ChatStreamEvent.Done -> {
                        _state.update { it.copy(isGenerating = false) }
                    }
                }
            }
            _state.update { it.copy(isGenerating = false) }
        }
    }
    
    fun stopGeneration() {
        chatJob?.cancel()
        _state.update { it.copy(isGenerating = false) }
    }
}
