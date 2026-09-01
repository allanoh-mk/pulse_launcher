package app.lawnchair.pulse.island

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

/**
 * Renders the current [IslandVisualState] as a capsule that morphs shape/size
 * with a spring so it feels like a single continuous liquid surface, per the
 * "one dominant motion at a time" convention in docs/13-ui-ux-design-references.md.
 */
@Composable
fun IslandOverlay(
    state: IslandVisualState,
    onTap: () -> Unit,
    onDismissExpanded: () -> Unit,
) {
    AnimatedContent(
        targetState = state,
        transitionSpec = {
            androidx.compose.animation.fadeIn(
                animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMedium),
            ) togetherWith androidx.compose.animation.fadeOut(
                animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMedium),
            )
        },
        label = "island-state",
    ) { visualState ->
        when (visualState) {
            is IslandVisualState.Hidden -> Spacer(modifier = Modifier.size(0.dp))
            is IslandVisualState.Compact -> CompactPill(visualState.activity, onTap)
            is IslandVisualState.Minimal -> MinimalPill(visualState.primary, visualState.secondary, onTap)
            is IslandVisualState.Expanded -> ExpandedCard(visualState, onDismissExpanded)
        }
    }
}

@Composable
private fun CompactPill(activity: IslandActivity, onTap: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(androidx.compose.ui.graphics.Color.Black)
            .pointerInput(activity.id) { detectTapGestures(onTap = { onTap() }) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = activity.title, color = androidx.compose.ui.graphics.Color.White, maxLines = 1)
    }
}

@Composable
private fun MinimalPill(primary: IslandActivity, secondary: IslandActivity, onTap: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(androidx.compose.ui.graphics.Color.Black)
            .pointerInput(primary.id, secondary.id) { detectTapGestures(onTap = { onTap() }) }
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
        Spacer(modifier = Modifier.width(10.dp))
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondary))
    }
}

@Composable
private fun ExpandedCard(state: IslandVisualState.Expanded, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(32.dp))
            .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.92f))
            .pointerInput(state.primary.id) { detectTapGestures(onTap = { onDismiss() }) }
            .padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (state.primary.type == IslandActivityType.ASSISTANT) {
                AssistantChatBubble()
            } else {
                Box(
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Box {
                    Text(text = state.primary.title, color = androidx.compose.ui.graphics.Color.White)
                    state.primary.subtitle?.let {
                        Text(text = it, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f))
                    }
                }
            }
        }
    }
}
