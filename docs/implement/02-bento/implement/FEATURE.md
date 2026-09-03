# Pulse Bento — Scrollable Personal Widget Screen

## Intent
Screen 2 is a vertically scrollable user-built widget environment. A polished default exists, but ownership belongs to the user.

## Content
1. Hosted Android App Widgets.
2. Lightweight Pulse-native widgets.
3. Static information cards.
4. Interactive control surfaces.

## Default
Weather → Music/Battery → Calendar → Connectivity/Screen Time → Smart Apps.

## Styling
Every item can expose size, placement, blur, corner curves, padding, wallpaper treatment, opacity, live/static behavior, and tap/double-tap/swipe/long-press actions.

## State
```kotlin
data class BentoItem(
    val id: String,
    val providerId: String,
    val spanX: Int,
    val spanY: Int,
    val order: Int,
    val style: BentoStyle,
    val interaction: InteractionMap
)
```

## Implementation
Build persisted vertical layout → native Pulse cards → AppWidgetHost integration → drag/reorder/resize → editor with undo → Android 12 Go profiling.

## Motion
Edit mode scale 0.98→1.00 over about 180 ms with a fast spatial spring. Reordering animates translation only; cards are not recreated.

## Exit criteria
Backup/restore reproduces the layout without widget identity corruption.