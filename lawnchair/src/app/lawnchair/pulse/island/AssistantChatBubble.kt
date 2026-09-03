package app.lawnchair.pulse.island

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.lawnchair.pulse.assistant.AssistantViewModel
import app.lawnchair.pulse.assistant.providers.ChatMessage

@Composable
fun AssistantChatBubble(modifier: Modifier = Modifier, viewModel: AssistantViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF1E1E1E))
            .padding(16.dp),
    ) {
        // Model Selector Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            state.availableModels.take(3).forEach { modelId ->
                val shortLabel = when {
                    modelId.contains("gemini") -> "Gemini Flash"
                    modelId.contains("claude") -> "Claude Sonnet"
                    modelId.contains("llama") -> "Llama 70B"
                    else -> modelId.substringAfterLast("/")
                }
                val isSelected = modelId == state.currentModelId
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF2C2C2E))
                        .clickable { viewModel.selectModel(modelId) }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = shortLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.LightGray,
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = false,
        ) {
            items(state.messages) { msg ->
                val isUser = msg.role == ChatMessage.Role.USER
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart,
                ) {
                    Text(
                        text = msg.content,
                        color = Color.White,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isUser) Color(0xFF007AFF) else Color(0xFF333333))
                            .padding(12.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF333333))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (state.currentInput.isEmpty()) {
                Text("Ask anything...", color = Color.Gray)
            }
            BasicTextField(
                value = state.currentInput,
                onValueChange = { viewModel.onInputChange(it) },
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                cursorBrush = SolidColor(Color.White),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { viewModel.sendMessage() }),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
