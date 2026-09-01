package app.lawnchair.pulse.workspace

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.lawnchair.pulse.data.db.PulseDatabase
import app.lawnchair.pulse.data.db.TileConfig
import app.lawnchair.pulse.data.db.TileGridLayout
import app.lawnchair.pulse.data.db.TileType
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
    val tiles by dao.observeAll().collectAsState(initial = emptyList())

    var seeded by remember { mutableStateOf(false) }
    if (tiles.isEmpty() && !seeded) {
        seeded = true
        scope.launch { dao.upsertAll(defaultStarterTiles()) }
    }

    val positions = remember(tiles) { TileGridLayout.packTiles(tiles) }
    val orderedTiles = remember(tiles) {
        tiles.sortedBy { it.sortOrder }
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
                        modifier = Modifier
                            .aspectRatio(tile.spanX.toFloat() / tile.spanY.toFloat())
                            .longPressDraggableHandle(),
                    )
                }
            }
        }
    }
}

@Composable
private fun TileCell(tile: TileConfig, isDragging: Boolean, modifier: Modifier = Modifier) {
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
        Text(
            text = tile.customLabel ?: tile.tileType.name,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun defaultStarterTiles(): List<TileConfig> = listOf(
    TileConfig(id = "clock", tileType = TileType.CLOCK, spanX = 2, spanY = 1, sortOrder = 0),
    TileConfig(id = "media", tileType = TileType.MEDIA, spanX = 2, spanY = 2, sortOrder = 1),
    TileConfig(id = "weather", tileType = TileType.WEATHER, spanX = 2, spanY = 1, sortOrder = 2),
    TileConfig(id = "assistant", tileType = TileType.ASSISTANT, spanX = 4, spanY = 1, sortOrder = 3),
)
