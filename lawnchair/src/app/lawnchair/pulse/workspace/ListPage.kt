package app.lawnchair.pulse.workspace

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.launch

/**
 * Slide 3: alphabetical vertical app list with a Niagara-style "wave" letter
 * index rail on the right edge. Dragging along the rail scrubs the list to
 * the matching letter section and magnifies nearby letters as feedback.
 */
@Composable
fun ListPage() {
    val context = LocalContext.current
    val apps by remember { AppListProvider.observeApps(context) }.collectAsState(initial = emptyList())
    val grouped = remember(apps) {
        apps.groupBy { it.label.firstOrNull()?.uppercaseChar()?.takeIf { c -> c.isLetter() } ?: '#' }
            .toSortedMap()
    }
    val letters = remember(grouped) { grouped.keys.toList() }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var focusedLetterIndex by remember { mutableStateOf<Int?>(null) }

    // Flattened index of where each letter's header sits in the LazyColumn,
    // so the rail can jump straight to it.
    val letterHeaderPositions = remember(grouped) {
        val positions = mutableMapOf<Char, Int>()
        var index = 0
        for ((letter, appsInGroup) in grouped) {
            positions[letter] = index
            index += 1 + appsInGroup.size
        }
        positions
    }

    val view = androidx.compose.ui.platform.LocalView.current

    Row(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(vertical = 8.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        ) {
            grouped.forEach { (letter, appsInGroup) ->
                item(key = "header_$letter") {
                    Text(
                        text = letter.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 4.dp),
                    )
                }
                items(appsInGroup, key = { it.packageName + it.user.hashCode() }) { app ->
                    AppRow(app)
                }
            }
        }

        AlphabetRail(
            letters = letters,
            focusedIndex = focusedLetterIndex,
            modifier = Modifier
                .width(28.dp)
                .fillMaxHeight()
                .padding(vertical = 24.dp, horizontal = 4.dp),
            onFocusChange = { fraction ->
                val index = AlphabetIndexMath.letterIndexForTouch(fraction, letters.size)
                if (index != focusedLetterIndex) {
                    focusedLetterIndex = index
                    val letter = letters.getOrNull(index)
                    val targetPosition = letter?.let { letterHeaderPositions[it] }
                    if (targetPosition != null) {
                        scope.launch { listState.scrollToItem(targetPosition) }
                    }
                    // Trigger a custom tactile tick
                    try {
                        view.performHapticFeedback(
                            android.view.HapticFeedbackConstants.CLOCK_TICK,
                        )
                    } catch (e: Exception) {}
                }
            },
            onFocusEnd = { focusedLetterIndex = null },
        )
    }
}

@Composable
private fun AppRow(app: PulseAppInfo) {
    val context = LocalContext.current
    val painter: Painter = rememberDrawablePainter(app.icon)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    setPackage(app.packageName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                runCatching { context.startActivity(intent) }
            }
            .padding(horizontal = 20.dp, vertical = 10.dp),
    ) {
        Image(
            painter = painter,
            contentDescription = app.label,
            modifier = Modifier
                .size(36.dp)
                .clip(MaterialTheme.shapes.small),
        )
        Text(
            text = app.label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp),
        )
    }
}

@Composable
private fun AlphabetRail(
    letters: List<Char>,
    focusedIndex: Int?,
    onFocusChange: (fraction: Float) -> Unit,
    modifier: Modifier = Modifier,
    onFocusEnd: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.pointerInput(letters) {
            detectVerticalDragGestures(
                onVerticalDrag = { change, _ ->
                    val fraction = (change.position.y / size.height).coerceIn(0f, 1f)
                    onFocusChange(fraction)
                },
                onDragEnd = { onFocusEnd() },
                onDragCancel = { onFocusEnd() },
            )
        },
    ) {
        letters.forEachIndexed { index, letter ->
            val waveScale = AlphabetIndexMath.waveScale(index, focusedIndex)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (index == focusedIndex) MaterialTheme.colorScheme.primaryContainer else androidx.compose.ui.graphics.Color.Transparent,
                    ),
            ) {
                Text(
                    text = letter.toString(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = MaterialTheme.typography.labelSmall.fontSize * waveScale,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
