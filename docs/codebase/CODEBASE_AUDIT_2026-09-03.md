# Pulse Codebase Audit — 2026-09-03

## Evidence scope
This audit uses the current repository tree plus direct inspection of build files, manifests, Pulse sources, workspace integration, feed bridge, search, music, and island code.

## 1. Gradle module graph
The root application project includes baseline-profile, hidden-api, SystemUI-derived modules, compatLib plus VQ/VR/VS/VT/VU, and iconloaderlib/searchuilib/animationlib from the SystemUI submodule.

Finding: this is a tightly integrated Launcher3/Lawnchair build, not a small standalone Compose app. Pulse should be introduced incrementally behind stable boundaries.

## 2. Manifest component trace
Active launcher infrastructure includes the HOME launcher activity, LawnchairApp application class, notification listener, workspace/settings/grid providers, and Quickstep manifest merges.

Pulse registrations found: CanvasActivity and IslandService.

Risk flags: MANAGE_EXTERNAL_STORAGE, cross-user management permissions, force-stop permission, status-bar service, SurfaceFlinger permissions, and special-use foreground service declarations need a separate release-policy review.

## 3. Pulse-specific source inventory
The app.lawnchair.pulse tree contains workspace, search, notifications, control center, Dynamic Island, music, wallpaper, assistant providers, focus mode, icon/font studio, Room-backed configuration, and unit tests.

## 4. Inherited Lawnchair systems
The app.lawnchair package contains Lawnchair customization while src/ and quickstep/ contain inherited Launcher3/Quickstep infrastructure. Preserve behind adapters: FeedBridge, All Apps/search, widget hosting, notification listener, icon packs, preferences/backup, Smartspace, gestures, and Quickstep.

## 5. compatLibVQ / compatLibVR
VQ is Android 10 compatibility and VR is Android 11 compatibility. Both remain in the root build and Quickstep dependency wiring.

Verdict: DELETE CANDIDATE — NOT SAFE TO REMOVE YET. First remove or conditionalize references, then build and smoke-test API 31+ variants.

## 6. Asset findings
Strong unused candidates: Black_ink_writing_Pulse_202609010218.mp4 and Black_ink_writing_pulse_2K_202609010218.jpeg at repository root. No direct source reference was found during inspection; PulseSplashScreen draws procedurally and does not load them.

Action: verify with a full reference scan before moving. Preferred destination if adopted: lawnchair/assets/pulse/onboarding/.

## 7. Google/Discover feed
app.lawnchair.FeedBridge is a real active integration boundary resolving external overlay/feed providers including Pixel/Nexus and Lawnfeed-style providers, with Smartspacer support. LawnchairLauncher wires the existing overlay separately.

Pulse implication: Pulse Feed is a separate Compose page. Legacy feed bridge and Pulse Feed are parallel systems, not yet unified.

## 8. First-launch media
Current first-launch behavior is a Compose-drawn PulseSplashScreen lasting about 4.3 seconds. The root MP4/JPEG are present but not wired.

Status: MEDIA PRESENT, INTEGRATION NOT PRESENT.

## 9. Three-screen map
WorkspaceController defines PAGE_COUNT = 3: page 0 FeedPage, page 1 TileGridPage, page 2 ListPage. LawnchairLauncher.onCreate calls PulseWorkspaceHost.attach(this).
Status: the three screens exist and are actually attached to the launcher.

## 10. High-level reality
- Three-screen host: IMPLEMENTED, needs hardening.
- Feed: PARTIAL.
- Bento: PARTIAL.
- List: PARTIAL.
- Unified search: PARTIAL.
- Google feed bridge: IMPLEMENTED separately, NOT UNIFIED.
- Notifications: PARTIAL.
- Control Center: PARTIAL.
- Dynamic Island: PARTIAL.
- Pulse Music: PARTIAL local playback.
- MetroList-level streaming: NOT IMPLEMENTED.
- Wallpaper natural scene motion: PARTIAL / not verified.
- Seven-step onboarding: NOT IMPLEMENTED.
- Root media onboarding: NOT IMPLEMENTED.
- Android 12 Go validation: NOT VERIFIED.

## Immediate engineering risks
1. Pulse Compose host is added directly to dragLayer, creating potential interaction layering conflicts.
2. PulseWorkspaceHost contains an accidental experimental long-press wallpaper-generation path that should not ship as-is.
3. Feed has mock screen-time data.
4. Search/file access policy needs tightening for Android 12+ and distribution.
5. Several manifest permissions need release compliance review.
6. Music currently uses MediaPlayer/local URIs, not a MetroList-equivalent service architecture.