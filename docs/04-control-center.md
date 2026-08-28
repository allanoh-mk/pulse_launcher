# 04 — Control Center

## Overview

A custom iOS-style control center overlay.
Triggered by **swipe down from top-right corner** (or configurable gesture).
Implemented as a `TYPE_APPLICATION_OVERLAY` window.
No root required.

## Layout

```
┌─────────────────────────────────┐
│  [WiFi] [BT] [Airplane] [Data]  │  ← Connectivity row
├─────────────────────────────────┤
│                                 │
│   ◉ Brightness          ☀️      │  ← Brightness slider (horizontal)
│                                 │
├─────────────────────────────────┤
│                                 │
│   🔊 Volume               🔊    │  ← Volume slider (horizontal)
│                                 │
├─────────────────────────────────┤
│                                 │
│   [Media Controls Card]         │  ← Synced with Island
│   [Album Art]  ⏮  ⏯  ⏭        │
│                                 │
├─────────────────────────────────┤
│                                 │
│   [Focus] [Flashlight] [QR] [📷]│  ← Quick toggles (2×2 grid)
│                                 │
├─────────────────────────────────┤
│  [App 1] [App 2] [App 3] [App 4]│  ← Quick app shortcuts
└─────────────────────────────────┘
```

## Toggles & Controls

| Control | API |
|---------|-----|
| WiFi | `ConnectivityManager` + `WifiManager` |
| Bluetooth | `BluetoothManager` |
| Airplane Mode | `Settings.Global.AIRPLANE_MODE_ON` |
| Mobile Data | `TelephonyManager` + `NetworkCapabilities` |
| Brightness | `Settings.System.SCREEN_BRIGHTNESS` |
| Volume | `AudioManager` |
| Flashlight | `CameraManager` / `Camera2` |
| Dark Mode | `Settings.System.UI_NIGHT_MODE` |
| Focus Mode | Custom (mutes selected apps) |
| QR Scanner | `Camera2` + ZXing |
| Screenshot | `MediaProjection` |
| Media Controls | `MediaSessionManager` (synced with island) |

## Styling

- Background: frosted glass (backdrop blur via Haze)
- Corner radius: 24dp
- Toggles: pill-shaped, filled when active (accent color)
- Sliders: custom Compose sliders with haptic detents
- All elements respect the **active icon style** (glass toggles, neon borders, etc.)
- Animation: slide down from top with spring (stiffness 200, damping 28)
- Dismiss: swipe up or tap outside

## Customization

- Reorder tiles (drag & drop in settings)
- Add/remove quick app shortcuts
- Toggle visibility of individual controls
- Change background opacity
- Choose: compact mode (fewer tiles) vs. full mode
