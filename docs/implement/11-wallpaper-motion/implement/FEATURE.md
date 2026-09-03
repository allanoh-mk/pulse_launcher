# Pulse Wallpaper Motion Engine

## Reference image
The supplied example is a sunset beach scene with silhouetted palm trees, water, clouds, and a crescent moon. It is an example only, not the default wallpaper.

## Desired motion
Nearly subconscious:
- palm fronds: tiny wind oscillation
- water: low-amplitude shimmer
- clouds: extremely slow drift
- sky: subtle luminance breathing

Trunks, shoreline, horizon, and moon remain stable unless analysis explicitly supports another effect.

## Pipeline
```text
wallpaper selected
→ one-time scene analysis
→ segmentation/depth/motion regions
→ motion map persisted
→ lightweight runtime renderer
```

Do not run a neural model continuously. Analyze when wallpaper changes, cache the compact result, then render from the motion map.

## Validation
Use before/after recordings on Android 12 Go hardware. Check frame pacing, memory spikes, thermal behavior, and segmentation errors.