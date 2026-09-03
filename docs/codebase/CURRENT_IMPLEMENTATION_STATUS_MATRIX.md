# Current Implementation Status Matrix

Legend: IMPLEMENTED, PARTIAL, NOT IMPLEMENTED, NOT VERIFIED.

| Requirement | Status | Evidence / next gate |
|---|---|---|
| Android 12 minimum | IMPLEMENTED | root Gradle minSdk 31 |
| Android 12 Go | NOT VERIFIED | real-device benchmark gate |
| Screen 1 Feed | PARTIAL | FeedPage.kt |
| Screen 2 Bento | PARTIAL | TileGridPage.kt + Room configs |
| Screen 3 List | PARTIAL | ListPage.kt |
| Shared three-screen navigation | IMPLEMENTED | WorkspaceController.kt |
| Launcher attachment | IMPLEMENTED | LawnchairLauncher calls PulseWorkspaceHost.attach |
| User-custom Feed layouts | NOT IMPLEMENTED | layout editor required |
| Bento user-built widget world | PARTIAL | persisted tiles; full host/editor incomplete |
| Niagara-style interactive notifications | NOT VERIFIED | direct action path audit needed |
| Smart Dock predictions | NOT VERIFIED | graph not found as implementation |
| Natural-language search | PARTIAL | dispatcher exists; full intent graph incomplete |
| Internet search | NOT VERIFIED | provider contract audit |
| Contacts search | PARTIAL | Contacts provider query |
| Files search | PARTIAL/RISK | MediaStore path; permission review |
| Calculator | PARTIAL | math evaluator exists |
| Currency | NOT VERIFIED | provider not confirmed |
| Google/Discover Feed | IMPLEMENTED separately | FeedBridge.kt; not unified |
| Control Center | PARTIAL | bridge + overlay |
| Dynamic Island | PARTIAL | state machine + service |
| Pulse Music local playback | PARTIAL | PulseMusicEngine.kt |
| MetroList-level online music | NOT IMPLEMENTED | research/compliance first |
| Wallpaper motion | PARTIAL | engine exists; natural motion incomplete |
| 7-screen onboarding | NOT IMPLEMENTED | build phase |
| First-launch MP4/JPEG | NOT IMPLEMENTED | assets not referenced |
| Performance modes | NOT VERIFIED | runtime governor audit |