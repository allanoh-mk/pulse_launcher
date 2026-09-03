package app.lawnchair.pulse.workspace

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import app.lawnchair.pulse.focus.FocusModeManager
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.lawnchair.pulse.data.db.PulseDatabase
import app.lawnchair.pulse.data.db.TileConfig
import app.lawnchair.pulse.data.db.TileGridLayout
import app.lawnchair.pulse.data.db.TileType
import app.lawnchair.pulse.data.repositories.WeatherRepository
import app.lawnchair.pulse.music.PulseMediaScanner
import app.lawnchair.pulse.music.PulseMusicEngine
import app.lawnchair.pulse.music.ui.PulseMusicBottomSheet
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

/**
 * Slide 2: the bento-style tile grid. Tiles are persisted in [PulseDatabase]
 * and packed into a 4-column layout via [TileGridLayout.packTiles]. Empty
 * state seeds a starter set of tiles on first run so the slide is never blank.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TileGridPage() {
    val context = LocalContext.current
    val dao = remember { PulseDatabase.INSTANCE.get(context).tileConfigDao() }
    val scope = rememberCoroutineScope()
    val isFocusModeActive by FocusModeManager.isFocusModeActive.collectAsState()
    val tiles by dao.observeAll().collectAsState(initial = emptyList())

    var seeded by remember { mutableStateOf(false) }
    if (tiles.isEmpty() && !seeded) {
        seeded = true
        scope.launch { dao.upsertAll(defaultStarterTiles()) }
    }

    val positions = remember(tiles) { TileGridLayout.packTiles(tiles) }
    val orderedTiles = remember(tiles) {
        tiles.filter {
            if (isFocusModeActive) {
                !FocusModeManager.hiddenTilesInFocusMode.contains(it.id)
            } else {
                true
            }
        }.sortedBy { it.sortOrder }
    }

    val lazyGridState = rememberLazyGridState()
    val reorderableState = rememberReorderableLazyGridState(lazyGridState) { from, to ->
        val mutable = orderedTiles.toMutableList()
        val fromIndex = mutable.indexOfFirst { it.id == from.key }
        val toIndex = mutable.indexOfFirst { it.id == to.key }
        if (fromIndex != -1 && toIndex != -1) {
            val moved = mutable.removeAt(fromIndex)
            mutable.add(toIndex, moved)
            val reindexed = mutable.mapIndexed { index, tile -> tile.copy(sortOrder = index) }
            scope.launch { dao.upsertAll(reindexed) }
        }
    }

    var showMusicSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            state = lazyGridState,
            columns = GridCells.Fixed(TileGridLayout.COLUMN_COUNT),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(orderedTiles, key = { it.id }, span = { tile ->
                GridItemSpan(tile.spanX.coerceAtMost(TileGridLayout.COLUMN_COUNT))
            }) { tile ->
                ReorderableItem(reorderableState, key = tile.id) { isDragging ->
                    TileCell(
                        tile = tile,
                        isDragging = isDragging,
                        onExpandMusic = { showMusicSheet = true },
                        modifier = Modifier
                            .aspectRatio(tile.spanX.toFloat() / tile.spanY.toFloat())
                            .longPressDraggableHandle(),
                    )
                }
            }
        }

        if (showMusicSheet) {
            PulseMusicBottomSheet(onDismissRequest = { showMusicSheet = false })
        }
    }
}

@Composable
private fun TileCell(
    tile: TileConfig,
    isDragging: Boolean,
    onExpandMusic: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (isDragging) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .padding(12.dp),
    ) {
        when (tile.tileType) {
            TileType.CLOCK -> ClockTile()
            TileType.WEATHER -> WeatherTile()
            TileType.MEDIA -> MediaTile(onExpand = onExpandMusic)
            TileType.ASSISTANT -> AssistantTile()
            TileType.DIGITAL_WELLBEING -> DigitalWellbeingTile()
            TileType.CLIPBOARD -> ClipboardTile()
            else -> {
                Text(
                    text = tile.customLabel ?: tile.tileType.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ClockTile(modifier: Modifier = Modifier) {
    BentoFlipCard(
        frontContent = { ClockTileFront() },
        backContent = { ClockTileBack() },
        modifier = modifier
    )
}

@Composable
private fun ClockTileBack(modifier: Modifier = Modifier) {
    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            nowMillis = System.currentTimeMillis()
            kotlinx.coroutines.delay(1000)
        }
    }
    val calendar = remember(nowMillis) { java.util.Calendar.getInstance().apply { timeInMillis = nowMillis } }
    val dateFormat = remember { java.text.SimpleDateFormat("EEEE, MMMM d", java.util.Locale.getDefault()) }
    androidx.compose.foundation.layout.Box(modifier = modifier, contentAlignment = androidx.compose.ui.Alignment.Center) {
        androidx.compose.material3.Text(
            text = dateFormat.format(calendar.time),
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ClockTileFront(modifier: Modifier = Modifier) {
    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            nowMillis = System.currentTimeMillis()
            delay(1000)
        }
    }
    val calendar = remember(nowMillis) { Calendar.getInstance().apply { timeInMillis = nowMillis } }
    val timeFormat = remember { SimpleDateFormat("h:mm", Locale.getDefault()) }
    val amPmFormat = remember { SimpleDateFormat("a", Locale.getDefault()) }

    Row(modifier = modifier, verticalAlignment = Alignment.Bottom) {
        Text(
            text = timeFormat.format(calendar.time),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = amPmFormat.format(calendar.time),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 6.dp),
        )
    }
}

@Composable
private fun WeatherTile(modifier: Modifier = Modifier) {
    BentoFlipCard(
        frontContent = { WeatherTileFront() },
        backContent = { WeatherTileBack() },
        modifier = modifier
    )
}

@Composable
private fun WeatherTileBack(modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Column(modifier = modifier, horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        androidx.compose.material3.Text(
            text = "3-Day Forecast",
            style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
        )
        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(4.dp))
        androidx.compose.material3.Text(
            text = "Coming soon...",
            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun WeatherTileFront(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val repository = remember { WeatherRepository(context) }
    var weatherData by remember { mutableStateOf<WeatherRepository.WeatherData?>(null) }

    LaunchedEffect(Unit) {
        try {
            weatherData = repository.getWeather()
        } catch (e: Exception) {
            // Ignore
        }
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = weatherData?.temperature?.toString()?.plus("°") ?: "--°",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = weatherData?.city ?: "--",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        )
    }
}

@Composable
private fun MediaTile(onExpand: () -> Unit = {}, modifier: Modifier = Modifier) {
    BentoFlipCard(
        frontContent = { MediaTileFront(onExpand) },
        backContent = { MediaTileBack() },
        modifier = modifier
    )
}

@Composable
private fun MediaTileBack(modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Box(modifier = modifier, contentAlignment = androidx.compose.ui.Alignment.Center) {
        androidx.compose.material3.Text(
            text = "Up Next",
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun MediaTileFront(onExpand: () -> Unit = {}, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val playbackState by PulseMusicEngine.state.collectAsState()

    LaunchedEffect(Unit) {
        PulseMusicEngine.initialize(context)
        if (playbackState.queue.isEmpty()) {
            val tracks = PulseMediaScanner.scanLocalTracks(context)
            PulseMusicEngine.playQueue(tracks, 0)
            PulseMusicEngine.pause()
        }
    }

    val track = playbackState.currentTrack
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .clickable { onExpand() },
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxSize(0.55f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            if (track?.coverUri != null && track.coverUri.isNotEmpty()) {
                AsyncImage(
                    model = track.coverUri,
                    contentDescription = "Track Cover",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = "Music",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = track?.title ?: "Pulse Music",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = track?.artist ?: "Tap to open",
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        )
        IconButton(
            onClick = { PulseMusicEngine.togglePlayPause() },
            modifier = Modifier.size(28.dp),
        ) {
            Icon(
                imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun AssistantTile(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "✨",
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Ask Pulse...",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
        )
    }
}

private fun defaultStarterTiles(): List<TileConfig> = listOf(
    TileConfig(id = "clock", tileType = TileType.CLOCK, spanX = 2, spanY = 1, sortOrder = 0),
    TileConfig(id = "media", tileType = TileType.MEDIA, spanX = 2, spanY = 2, sortOrder = 1),
    TileConfig(id = "weather", tileType = TileType.WEATHER, spanX = 2, spanY = 1, sortOrder = 2),
    TileConfig(id = "assistant", tileType = TileType.ASSISTANT, spanX = 4, spanY = 1, sortOrder = 3),
    TileConfig(id = "digital_wellbeing", tileType = TileType.DIGITAL_WELLBEING, spanX = 2, spanY = 1, sortOrder = 4),
    TileConfig(id = "clipboard", tileType = TileType.CLIPBOARD, spanX = 2, spanY = 1, sortOrder = 5),
)


@Composable
private fun DigitalWellbeingTile(modifier: Modifier = Modifier) {
    BentoFlipCard(
        frontContent = { DigitalWellbeingTileFront() },
        backContent = { DigitalWellbeingTileBack() },
        modifier = modifier
    )
}

@Composable
private fun DigitalWellbeingTileFront(modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = androidx.compose.material.icons.Icons.Default.GraphicEq, // Placeholder icon
            contentDescription = "Screen Time",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "4h 12m",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Screen Time",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun DigitalWellbeingTileBack(modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Most Used",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "1. Chrome (1h 30m)\n2. YouTube (1h)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun ClipboardTile(modifier: Modifier = Modifier) {
    BentoFlipCard(
        frontContent = { ClipboardTileFront() },
        backContent = { ClipboardTileBack() },
        modifier = modifier
    )
}

@Composable
private fun ClipboardTileFront(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var clipboardText by remember { mutableStateOf("No clips yet") }

    LaunchedEffect(Unit) {
        val clipboardManager = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
        val clip = clipboardManager?.primaryClip
        if (clip != null && clip.itemCount > 0) {
            clipboardText = clip.getItemAt(0).text?.toString() ?: "Image/URI"
        }
    }

    androidx.compose.foundation.layout.Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Clipboard",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = clipboardText,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ClipboardTileBack(modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = "Tap to clear",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
    }
}
