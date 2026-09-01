package app.lawnchair.pulse.workspace

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import app.lawnchair.LawnchairLauncher

import app.lawnchair.pulse.controlcenter.ControlCenterViewModel
import app.lawnchair.pulse.controlcenter.ControlCenterOverlay
import app.lawnchair.pulse.search.SearchViewModel
import app.lawnchair.pulse.search.UnifiedSearchOverlay
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.runtime.remember

import androidx.compose.ui.platform.LocalContext

import app.lawnchair.pulse.splash.PulseSplashScreen
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

object PulseWorkspaceHost {
    fun attach(launcher: LawnchairLauncher) {
        val workspace = ComposeView(launcher).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val context = LocalContext.current
                val controlCenterViewModel = remember { ControlCenterViewModel(context) }
                val searchViewModel = remember { SearchViewModel(context) }
                
                var showSplash by remember { mutableStateOf(true) }

                Box(modifier = Modifier.fillMaxSize()) {
                    PulseWorkspace(controlCenterViewModel, searchViewModel)
                    UnifiedSearchOverlay(searchViewModel)
                    ControlCenterOverlay(controlCenterViewModel)
                    
                    if (showSplash) {
                        PulseSplashScreen(onAnimationFinished = { showSplash = false })
                    }
                }
            }
        }
        launcher.dragLayer.addView(workspace, android.view.ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))
    }
}
