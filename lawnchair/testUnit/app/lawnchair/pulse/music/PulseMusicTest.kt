package app.lawnchair.pulse.music

import app.lawnchair.pulse.music.model.PulsePlaybackState
import app.lawnchair.pulse.music.model.PulseTrack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PulseMusicTest {

    private val track1 = PulseTrack(
        id = "t1",
        title = "Neon Glow",
        artist = "Pulse",
        album = "Synth City",
        durationMs = 200000L,
    )

    private val track2 = PulseTrack(
        id = "t2",
        title = "Midnight Drive",
        artist = "Lofi Boy",
        album = "Nightfall",
        durationMs = 150000L,
    )

    private val testQueue = listOf(track1, track2)

    @Before
    fun setUp() {
        PulseMusicEngine.playQueue(testQueue, 0)
    }

    @Test
    fun `playQueue initializes queue and sets current track`() {
        val state = PulseMusicEngine.state.value
        assertEquals(2, state.queue.size)
        assertEquals(0, state.queueIndex)
        assertEquals("Neon Glow", state.currentTrack?.title)
        assertTrue(state.isPlaying)
    }

    @Test
    fun `togglePlayPause updates isPlaying state`() {
        PulseMusicEngine.pause()
        assertFalse(PulseMusicEngine.state.value.isPlaying)

        PulseMusicEngine.resume()
        assertTrue(PulseMusicEngine.state.value.isPlaying)

        PulseMusicEngine.togglePlayPause()
        assertFalse(PulseMusicEngine.state.value.isPlaying)
    }

    @Test
    fun `next advances to second track in queue`() {
        PulseMusicEngine.next()
        val state = PulseMusicEngine.state.value
        assertEquals(1, state.queueIndex)
        assertEquals("Midnight Drive", state.currentTrack?.title)
    }

    @Test
    fun `previous wraps back or resets position`() {
        PulseMusicEngine.next() // move to track 2
        assertEquals(1, PulseMusicEngine.state.value.queueIndex)

        PulseMusicEngine.previous()
        assertEquals(0, PulseMusicEngine.state.value.queueIndex)
        assertEquals("Neon Glow", PulseMusicEngine.state.value.currentTrack?.title)
    }

    @Test
    fun `seekTo clamps position between zero and duration`() {
        PulseMusicEngine.seekTo(50000L)
        assertEquals(50000L, PulseMusicEngine.state.value.positionMs)

        // Beyond duration clamp
        PulseMusicEngine.seekTo(300000L)
        assertEquals(PulseMusicEngine.state.value.durationMs, PulseMusicEngine.state.value.positionMs)
    }

    @Test
    fun `cycleRepeatMode cycles through OFF ALL and ONE`() {
        assertEquals(PulsePlaybackState.RepeatMode.OFF, PulseMusicEngine.state.value.repeatMode)

        PulseMusicEngine.cycleRepeatMode()
        assertEquals(PulsePlaybackState.RepeatMode.ALL, PulseMusicEngine.state.value.repeatMode)

        PulseMusicEngine.cycleRepeatMode()
        assertEquals(PulsePlaybackState.RepeatMode.ONE, PulseMusicEngine.state.value.repeatMode)

        PulseMusicEngine.cycleRepeatMode()
        assertEquals(PulsePlaybackState.RepeatMode.OFF, PulseMusicEngine.state.value.repeatMode)
    }

    @Test
    fun `progressFraction calculates accurate ratio`() {
        val state = PulsePlaybackState(
            currentTrack = track1,
            isPlaying = true,
            positionMs = 50000L,
            durationMs = 200000L,
        )
        assertEquals(0.25f, state.progressFraction, 0.001f)
    }

    @Test
    fun `demo tracks provider has at least 3 tracks`() {
        val demo = PulseMediaScanner.getDemoTracks()
        assertTrue(demo.size >= 3)
        assertNotNull(demo[0].mediaUri)
    }
}
