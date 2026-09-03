package app.lawnchair.pulse.canvas

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CanvasActivity : ComponentActivity() {

    // Simple Letter Mapping
    private val appMap = mapOf(
        "C" to Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA),
        "M" to Intent(Intent.ACTION_VIEW, android.net.Uri.parse("geo:0,0?q=maps"))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var path by remember { mutableStateOf(Path()) }
            var isDrawing by remember { mutableStateOf(false) }
            val coroutineScope = rememberCoroutineScope()

            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            isDrawing = true
                            path = Path().apply { moveTo(offset.x, offset.y) }
                        },
                        onDrag = { change, _ ->
                            path.lineTo(change.position.x, change.position.y)
                            // Trigger recomposition
                            path = Path().apply { addPath(path) }
                        },
                        onDragEnd = {
                            isDrawing = false
                            coroutineScope.launch {
                                // Wait a moment for visual feedback
                                delay(300)
                                recognizeAndLaunch(path)
                                path = Path()
                                finish() // Close canvas after drawing
                            }
                        }
                    )
                }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawPath(
                        path = path,
                        color = Color.White,
                        style = Stroke(
                            width = 20f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        }
    }

    private fun recognizeAndLaunch(path: Path) {
        // A real implementation would use ML Kit Digital Ink Recognition.
        // For now, we mock the recognition. We can assume the user drew a 'C'.
        val bounds = path.getBounds()
        val recognizedLetter = if (bounds.width > bounds.height * 1.5) "M" else "C"
        
        Toast.makeText(this, "Recognized Gesture: $recognizedLetter", Toast.LENGTH_SHORT).show()
        
        val intent = appMap[recognizedLetter]
        if (intent != null) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "App not found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
