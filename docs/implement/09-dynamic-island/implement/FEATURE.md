# Pulse Dynamic Island

## States
COMPACT → ACTIVE → EXPANDED → MINI → DISMISSED

## Events
Music, timer, charging, Bluetooth, downloads, and other supported activities.

## Compatibility
Pulse supports overlay mode when allowed, launcher-surface mode when overlays are unavailable, and a no-crash fallback.

## Motion frames
Compact press:
- 0 ms: scale 1.00
- 60 ms: scale 1.025
- 140 ms: width expansion begins
- 240 ms: content crossfade
- 320 ms: settle

## Starting code
```kotlin
val anim = SpringAnimation(view, DynamicAnimation.SCALE_X, 1f)
anim.spring.dampingRatio = 0.9f
anim.spring.stiffness = 700f
anim.start()
```

Tune against device recordings. Reuse containers and avoid per-frame bitmap allocation.