# Pulse Music — Integrated Music Surface

## Intent
Pulse Music is not presented as a separate traditional launcher app. It is an immersive internal surface integrated with Search, Feed, Bento, and Dynamic Island.

## Research baseline
MetroList demonstrates playback, background playback, caching/download behavior, lyrics, search, library management, playlists, account-related features, and extensive theming. Its repository is GPL-3.0 and describes the project as maintenance mode.

## License gate
Do not copy MetroList code into Pulse without a license review. GPL-3.0 obligations can affect derivative or combined distributions. Inspiration is not permission to copy code or assets.

## Audit boundaries
Playback engine, network/data clients, cache, database, lyrics, search, UI, and authentication/account handling.

## Integration
Search can return Pulse Music cards. Bento hosts playback. Dynamic Island reflects active playback. Feed can show recent listening.

## Compliance
Validate service terms, region behavior, and distribution implications before shipping.