# 03 — Dynamic Island

## Overview

An iOS-level Dynamic Island implemented as a `TYPE_APPLICATION_OVERLAY`
foreground service. It's not a notification pill — it's a **live activity
surface** with a digital assistant baked in.

## States

| State | Size | Trigger | Content |
|-------|------|---------|---------|
| **Minimal** | Tiny dot/pill | 2+ concurrent activities | Left dot + right dot |
| **Compact** | Small pill (1–2 pieces) | 1 active activity | Left info + right info |
| **Expanded** | Full-width card (~80% screen) | Long-press / significant update | Rich controls, media, assistant |
| **Hidden** | None | No active activities | (island disappears) |

## Activity Types

| Activity | Compact Shows | Expanded Shows |
|----------|--------------|----------------|
| **Music** | Album art (left) + track title (right) | Full player: art, title, artist, scrubber, play/pause/skip, volume |
| **Timer** | Countdown (left) + label (right) | Large countdown, start/pause/reset, ring animation |
| **Stopwatch** | Elapsed time (left) + lap (right) | Large timer, lap list, start/stop/lap |
| **Call** | Caller name (left) + duration (right) | Caller info, mute/speaker/hangup, video toggle |
| **Navigation** | Turn arrow (left) + distance (right) | Mini map, ETA, reroute, destination |
| **Charging** | Battery % (left) + bolt icon (right) | Battery %, time to full, health info |
| **Screen Record** | Red dot (left) + duration (right) | Stop button, duration |
| **Privacy** | Mic/Camera icon (left) + app name (right) | "App X is using your microphone" + stop |
| **Live Activity** | App-specific (left) + (right) | App-specific rich UI (ride, delivery, sports, flight) |
| **Assistant** | Mic icon (left) + "Listening..." (right) | Full chat UI with LLM |

## Multi-Activity Handling

- Max 2 activities visible in compact state (left + right)
- 3+ activities → minimal state (dots)
- Priority: Call > Nav > Music > Timer > Other
- Tap left side → expand left activity
- Tap right side → expand right activity
- Long-press → expand (shows both if 2 active)

## Digital Assistant (Built-In)

### Trigger
- Tap the mic zone (right side of compact, when no other activity)
- Long-press island → assistant mode
- Voice wake word (optional, Porcupine)

### Behavior
- Expanded island becomes a **chat interface**
- Context-aware: knows foreground app, current media, pending notifications, time, location
- Quick actions: "play my workout playlist", "what's next on my calendar", "summarize my notifications"
- Voice input: `SpeechRecognizer` or wake word
- Voice output: `TextToSpeech` (optional)
- Backend: Gemini API (primary) + Ollama (offline fallback)
- Can trigger actions: open app, set timer, send message, toggle settings

### UI in Expanded State
- Chat bubbles (user right, assistant left)
- Typing indicator (3 dots animation)
- Quick suggestion chips below input
- Input: text field + mic button
- Max 10 messages visible, scrollable
- "Close" button → collapses island

## Animations

- **Appear:** Spring from top notch area (scale 0 → 1, slight overshoot)
- **Compact → Expanded:** Height spring + content fade-in (200ms)
- **Expanded → Compact:** Height spring + content fade-out (150ms)
- **Activity swap:** Crossfade + slight horizontal slide (100ms)
- **Multiple activities:** Dots pulse in sequence
- **Music waveform:** Animated bars in compact state (subtle)
- **Charging:** Bolt icon pulses slowly
- **All transitions:** Spring physics (stiffness: 300, damping: 25)

## Technical

- `ForegroundService` with `TYPE_APPLICATION_OVERLAY` window
- Window: full-width, top-anchored, height animated
- Renders in **Jetpack Compose** (smoothest animations)
- Listens to:
  - `MediaSessionManager` (music)
  - `TelephonyManager` (calls)
  - `NotificationListenerService` (notifications, privacy)
  - `BatteryManager` (charging)
  - `LocationManager` (navigation)
  - `AlarmManager` (timers)
  - Custom `IslandActivity` contract (third-party / personal apps)

## Island Activity Contract (For Personal Apps)

```kotlin
// Any app can register a live activity with the island
data class IslandActivity(
    val id: String,
    val type: ActivityType,       // MUSIC, TIMER, CALL, NAV, CUSTOM
    val title: String,
    val compactLeft: String,      // or drawable name
    val compactRight: String,
    val expandedContent: Bundle,  // app-specific UI data
    val priority: Int,            // 1 (highest) – 5 (lowest)
    val autoDismiss: Boolean      // true = dismiss when activity ends
)
```

Apps send via `ContentProvider` or `Broadcast`.
Island service picks up, renders, manages lifecycle.
