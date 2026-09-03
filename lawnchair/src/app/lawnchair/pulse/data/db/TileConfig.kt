package app.lawnchair.pulse.data.db

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * One cell in the bento-grid Tile workspace slide.
 *
 * @param spanX/spanY cell span in grid units (1, 2 or 4). Validated by
 *   [TileGridLayout] before being persisted.
 * @param sortOrder stable ordering key used to render tiles left-to-right,
 *   top-to-bottom, and to drive reorder-drag persistence.
 */
@Immutable
@Entity(tableName = "pulse_tiles")
data class TileConfig(
    @PrimaryKey val id: String,
    val tileType: TileType,
    val spanX: Int,
    val spanY: Int,
    val packageName: String? = null,
    val customLabel: String? = null,
    val sortOrder: Int,
)

enum class TileType {
    APP,
    MEDIA,
    WEATHER,
    ASSISTANT,
    CLOCK,
    DIGITAL_WELLBEING,
    CLIPBOARD,
}

/**
 * Pure validation/layout logic extracted from the entity so the grid math is
 * unit-testable without Room or Android framework dependencies.
 */
object TileGridLayout {
    const val COLUMN_COUNT = 4
    val VALID_SPANS = setOf(1, 2, 4)

    fun isValidSpan(spanX: Int, spanY: Int): Boolean =
        spanX in VALID_SPANS && spanY in VALID_SPANS && spanX <= COLUMN_COUNT

    /**
     * Packs tiles into row-major grid coordinates, respecting [TileConfig.sortOrder]
     * and skipping cells already occupied by a wider/taller tile above it.
     * Returns a map of tile id -> (column, row).
     */
    fun packTiles(tiles: List<TileConfig>): Map<String, Pair<Int, Int>> {
        val ordered = tiles.sortedBy { it.sortOrder }
        val occupied = HashSet<Long>()
        fun cellKey(col: Int, row: Int): Long = col.toLong() * 100_000 + row
        fun isFree(col: Int, row: Int, spanX: Int, spanY: Int): Boolean {
            if (col + spanX > COLUMN_COUNT) return false
            for (dx in 0 until spanX) {
                for (dy in 0 until spanY) {
                    if (occupied.contains(cellKey(col + dx, row + dy))) return false
                }
            }
            return true
        }

        fun occupy(col: Int, row: Int, spanX: Int, spanY: Int) {
            for (dx in 0 until spanX) {
                for (dy in 0 until spanY) {
                    occupied.add(cellKey(col + dx, row + dy))
                }
            }
        }

        val result = LinkedHashMap<String, Pair<Int, Int>>()
        for (tile in ordered) {
            var row = 0
            var placed = false
            while (!placed) {
                for (col in 0 until COLUMN_COUNT) {
                    if (isFree(col, row, tile.spanX, tile.spanY)) {
                        occupy(col, row, tile.spanX, tile.spanY)
                        result[tile.id] = col to row
                        placed = true
                        break
                    }
                }
                row++
            }
        }
        return result
    }
}
