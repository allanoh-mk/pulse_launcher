package app.lawnchair.pulse.workspace

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import app.lawnchair.LawnchairLauncher
import app.lawnchair.pulse.controlcenter.ControlCenterOverlay
import app.lawnchair.pulse.controlcenter.ControlCenterViewModel
import app.lawnchair.pulse.search.SearchViewModel
import app.lawnchair.pulse.search.UnifiedSearchOverlay
import app.lawnchair.pulse.splash.PulseSplashScreen

object PulseWorkspaceHost {
    fun attach(launcher: LawnchairLauncher) {
        val workspace = ComposeView(launcher).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val context = LocalContext.current
                val controlCenterViewModel = remember { ControlCenterViewModel(context) }
                val searchViewModel = remember { SearchViewModel(context) }

                var showSplash by remember { mutableStateOf(true) }

                Box(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
            detectTapGestures(
                onLongPress = {
                    scope.launch {
                        // Generate AI wallpaper based on Monet palette
                        AiWallpaperEngine.generateWallpaper(context, "A beautiful abstract landscape")
                    }
                }
            )
        }) {
                    PulseWorkspace(controlCenterViewModel, searchViewModel)
                    UnifiedSearchOverlay(searchViewModel)
                    ControlCenterOverlay(controlCenterViewModel)

                    if (showSplash) {
                        PulseSplashScreen(onAnimationFinish = { showSplash = false })
                    }
                }
            }
        }
        launcher.dragLayer.addView(workspace, android.view.ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))
    }
}
