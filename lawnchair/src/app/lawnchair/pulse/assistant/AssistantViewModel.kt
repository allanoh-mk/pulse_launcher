package app.lawnchair.pulse.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lawnchair.pulse.assistant.providers.AiProvider
import app.lawnchair.pulse.assistant.providers.ChatMessage
import app.lawnchair.pulse.assistant.providers.ChatStreamEvent
import app.lawnchair.pulse.assistant.providers.NineRouterProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AssistantAction(
    val type: ActionType,
    val payload: String = "",
) {
    enum class ActionType {
        PLAY_MUSIC,
        PAUSE_MUSIC,
        NEXT_TRACK,
        PREVIOUS_TRACK,
        TOGGLE_FLASHLIGHT,
        OPEN_APP,
        NONE,
    }
}

data class AssistantState(
    val messages: List<ChatMessage> = emptyList(),
    val isGenerating: Boolean = false,
    val currentInput: String = "",
    val error: String? = null,
    val currentProviderId: String = NineRouterProvider.PROVIDER_ID,
    val currentModelId: String = NineRouterProvider.MODEL_GEMINI_FLASH,
    val availableModels: List<String> = NineRouterProvider.PRESET_MODELS,
    val lastAction: AssistantAction? = null,
)

class AssistantViewModel : ViewModel() {
    private val _state = MutableStateFlow(AssistantState())
    val state: StateFlow<AssistantState> = _state.asStateFlow()

    private var provider: AiProvider = NineRouterProvider()
    private var chatJob: Job? = null

    fun selectModel(modelId: String) {
        provider.modelId = modelId
        _state.update { it.copy(currentModelId = modelId) }
    }

    fun setApiKey(key: String) {
        provider.apiKey = key
    }

    fun onInputChange(input: String) {
        _state.update { it.copy(currentInput = input) }
    }

    /**
     * Checks if the user command is a direct on-device action (e.g. music or system control)
     * and returns the parsed action, or null if it should be sent to the LLM.
     */
    fun parseDeviceAction(prompt: String): AssistantAction? {
        val lower = prompt.trim().lowercase()

        return when {
            lower.startsWith("play ") -> {
                val songQuery = prompt.substring(5).trim()
                AssistantAction(AssistantAction.ActionType.PLAY_MUSIC, songQuery)
            }
            lower == "play" || lower == "resume" -> {
                AssistantAction(AssistantAction.ActionType.PLAY_MUSIC, "")
            }
            lower == "pause" || lower == "stop music" -> {
                AssistantAction(AssistantAction.ActionType.PAUSE_MUSIC)
            }
            lower == "next" || lower == "next song" || lower == "skip" -> {
                AssistantAction(AssistantAction.ActionType.NEXT_TRACK)
            }
            lower == "previous" || lower == "prev" || lower == "previous song" -> {
                AssistantAction(AssistantAction.ActionType.PREVIOUS_TRACK)
            }
            lower.contains("flashlight") || lower.contains("torch") -> {
                AssistantAction(AssistantAction.ActionType.TOGGLE_FLASHLIGHT)
            }
            lower.startsWith("open ") -> {
                val appName = prompt.substring(5).trim()
                AssistantAction(AssistantAction.ActionType.OPEN_APP, appName)
            }
            else -> null
        }
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
                error = null,
                lastAction = null,
            )
        }

        // Check for immediate device/assistant actions first
        val action = parseDeviceAction(userText)
        if (action != null) {
            val responseText = when (action.type) {
                AssistantAction.ActionType.PLAY_MUSIC -> if (action.payload.isNotEmpty()) "Playing \"${action.payload}\" in Pulse Music." else "Resuming playback."
                AssistantAction.ActionType.PAUSE_MUSIC -> "Paused playback."
                AssistantAction.ActionType.NEXT_TRACK -> "Skipping to next track."
                AssistantAction.ActionType.PREVIOUS_TRACK -> "Playing previous track."
                AssistantAction.ActionType.TOGGLE_FLASHLIGHT -> "Toggling flashlight."
                AssistantAction.ActionType.OPEN_APP -> "Opening ${action.payload}."
                AssistantAction.ActionType.NONE -> ""
            }
            _state.update {
                it.copy(
                    messages = it.messages + ChatMessage(ChatMessage.Role.ASSISTANT, responseText),
                    isGenerating = false,
                    lastAction = action,
                )
            }
            return
        }

        // Otherwise stream response from AI provider
        val assistantMessageIndex = _state.value.messages.size
        _state.update { it.copy(messages = it.messages + ChatMessage(ChatMessage.Role.ASSISTANT, "")) }

        chatJob?.cancel()
        chatJob = viewModelScope.launch {
            val history = _state.value.messages.take(assistantMessageIndex)
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
