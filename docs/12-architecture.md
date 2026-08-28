# 12 — Architecture

## Module Structure

```
┌─────────────────────────────────────────────────────────┐
│                    Pulse Launcher                        │
├─────────────┬──────────────────┬────────────────────────┤
│   app/      │     island/      │       shared/          │
│  (Launcher) │  (Dynamic Island)│   (Common)             │
├─────────────┼──────────────────┼────────────────────────┤
│ Workspace   │ IslandService    │ Models                 │
│ FeedPage    │ CompactState     │ IconConfig             │
│ TileGrid    │ ExpandedState    │ ThemeConfig            │
│ ListPage    │ ActivityTypes    │ GestureConfig          │
│ Search      │ AssistantModule  │ HapticPatterns         │
│ IconStudio  │ Animations       │ DB (Room)              │
│ ThemeEngine │                  │ Utils                  │
│ ControlCntr │                  │                        │
│ Gestures    │                  │                        │
│ Haptics     │                  │                        │
└─────────────┴──────────────────┴────────────────────────┘
```

## Communication

| Between | Mechanism |
|---------|-----------|
| app ↔ island | `ContentProvider` + `BroadcastReceiver` |
| island ↔ system | `NotificationListenerService`, `MediaSessionManager`, `TelephonyManager` |
| app ↔ control center | Direct (same process, Compose state) |
| app ↔ assistant | Direct (same process) + API calls |
| island ↔ assistant | Direct (same process) |

## Data Layer

- **Room** (SQLite) for:
  - Icon config + per-app overrides
  - Theme config
  - Gesture config
  - Feed content cache
  - Assistant conversation history
  - Recent searches
  - Focus mode schedules
- **DataStore** (Preferences) for:
  - Active slide
  - Last-used settings
  - Toggle states

## State Management

- **Compose** `StateFlow` / `MutableStateFlow` for UI state
- **ViewModel** per major screen (Workspace, Search, Island, Control Center)
- **Single source of truth:** `PulseState` object holds:
  - Current theme
  - Current icon config
  - Active island activities
  - Feed items
  - Focus mode

## Performance Targets

| Metric | Target |
|--------|--------|
| Cold start | < 1.5s |
| Slide swipe | 60fps, < 16ms/frame |
| Icon render (glass) | < 8ms per icon |
| Search (local) | < 100ms |
| Island state change | < 200ms (animation) |
| Memory | < 200MB RSS |
| Battery | < 2% per day (idle) |
