package app.lawnchair.pulse.workspace

import android.app.WallpaperManager
import android.os.VibrationEffect
import android.os.VibratorManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter

import app.lawnchair.pulse.controlcenter.ControlCenterViewModel
import app.lawnchair.pulse.search.SearchViewModel
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.pointerInput

private const val PAGE_COUNT = 3
private const val WALLPAPER_PARALLAX = 0.7f

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PulseWorkspace(
    controlCenterViewModel: ControlCenterViewModel,
    searchViewModel: SearchViewModel
) {
    val context = LocalContext.current
    val view = LocalView.current
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { PAGE_COUNT })

    LaunchedEffect(pagerState, view) {
        snapshotFlow {
            pagerState.currentPage + pagerState.currentPageOffsetFraction
        }.collect { pageOffset ->
            val normalizedOffset = (pageOffset / (PAGE_COUNT - 1)).coerceIn(0f, 1f)
            WallpaperManager.getInstance(context).setWallpaperOffsets(
                view.windowToken,
                normalizedOffset * WALLPAPER_PARALLAX,
                0.5f,
            )
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .filter { it in 0 until PAGE_COUNT }
            .distinctUntilChanged()
            .drop(1)
            .collect {
                context.getSystemService(VibratorManager::class.java)
                    ?.defaultVibrator
                    ?.vibrate(
                        VibrationEffect.startComposition()
                            .addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 1f, 0)
                            .compose(),
                    )
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { change, dragAmount ->
                        if (dragAmount > 10f) { // Significant swipe down
                            val isTopRight = change.position.y < 200f && change.position.x > size.width * 0.7f
                            if (isTopRight) {
                                controlCenterViewModel.setVisible(true)
                            } else {
                                searchViewModel.setVisible(true)
                            }
                            change.consume()
                        }
                    }
                )
            },
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            when (page) {
                0 -> FeedPage()
                1 -> TileGridPage()
                else -> ListPage()
            }
        }
    }
}
