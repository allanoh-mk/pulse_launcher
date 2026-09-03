package app.lawnchair.pulse.island

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.lawnchair.pulse.music.PulseMusicEngine
import app.lawnchair.pulse.music.ui.PulseWavyProgressBar
import coil.compose.AsyncImage

/**
 * Renders the current [IslandVisualState] as a capsule that morphs shape/size
 * with a spring so it feels like a single continuous liquid surface, per the
 * "one dominant motion at a time" convention in docs/13-ui-ux-design-references.md.
 */
@Composable
fun IslandOverlay(
    state: IslandVisualState,
    onTap: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = state,
        modifier = modifier,
        transitionSpec = {
            androidx.compose.animation.fadeIn(
                animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMedium),
            ) togetherWith androidx.compose.animation.fadeOut(
                animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMedium),
            )
        },
        label = "islandStateTransition",
    ) { visualState ->
        when (visualState) {
            is IslandVisualState.Hidden -> Unit
            is IslandVisualState.Compact -> CompactPill(visualState.activity, onTap)
            is IslandVisualState.Minimal -> MinimalPill(visualState.primary, visualState.secondary, onTap)
            is IslandVisualState.Expanded -> ExpandedCard(visualState, onDismiss)
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
        if (activity.type == IslandActivityType.MEDIA) {
            Icon(
                imageVector = Icons.Default.GraphicEq,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp),
            )
        } else {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
            )
        }
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
        if (state.primary.type == IslandActivityType.ASSISTANT) {
            AssistantChatBubble()
        } else if (state.primary.type == IslandActivityType.MEDIA) {
            val musicState by PulseMusicEngine.state.collectAsState()
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (musicState.currentTrack?.coverUri != null && musicState.currentTrack!!.coverUri!!.isNotEmpty()) {
                            AsyncImage(
                                model = musicState.currentTrack!!.coverUri,
                                contentDescription = "Cover",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = state.primary.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                        )
                        Text(
                            text = state.primary.subtitle ?: "Pulse Music",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                PulseWavyProgressBar(
                    progress = musicState.progressFraction,
                    isPlaying = musicState.isPlaying,
                    onSeek = { fraction ->
                        val seekPos = (musicState.durationMs * fraction).toLong()
                        PulseMusicEngine.seekTo(seekPos)
                    },
                    barHeight = 20.dp,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { PulseMusicEngine.previous() }) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = Color.White)
                    }
                    IconButton(
                        onClick = { PulseMusicEngine.togglePlayPause() },
                        modifier = Modifier.size(44.dp).background(Color.White, CircleShape),
                    ) {
                        Icon(
                            if (musicState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (musicState.isPlaying) "Pause" else "Play",
                            tint = Color.Black,
                        )
                    }
                    IconButton(onClick = { PulseMusicEngine.next() }) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = Color.White)
                    }
                }
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary),
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
