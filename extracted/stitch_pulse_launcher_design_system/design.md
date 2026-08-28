# Pulse Launcher — Design System

*Version 0.1 — Design vision (hardware-unconstrained). Performance tiering is handled separately in `performance.md`.*

## 1. Design Thesis

**"Calm, glanceable, physical."**

Pulse is not a grid of icons — it's a small operating layer with its own material logic. Every surface (icon, tile, island, panel) behaves like it has weight, depth, and light. The interface should feel *tactile* — things press, expand, glow, and settle — while staying legible and fast to scan. Borrow iOS's confidence in motion and depth, Niagara's restraint in layout, Nova's depth of customization, and Mur's boldness of tiles.

---

## 2. Color System

### 2.1 Dynamic Accent Engine

Pulse's defining visual trait is a **single accent color** that propagates through the entire UI — buttons, active states, progress rings, selected tabs, the Island's glow, glass tints, focus rings, and chart lines all derive from one source color.

**Accent sources (user-selectable):**
| Source | Behavior |
|---|---|
| **Manual** | User picks a hex/HSL value from a color wheel in Settings → Themes |
| **Wallpaper-derived (Material You)** | Accent extracted from the dominant/vibrant color of the current wallpaper via palette quantization; re-extracts automatically on wallpaper change |
| **Time-based** | Accent hue rotates slowly across the day — cool blue at dawn, warm gold at noon, magenta at dusk, deep indigo at night (a slow, continuous hue-shift, not discrete jumps) |
| **Per-focus-mode** | Each Focus Mode (Work / Sleep / Custom) carries its own locked accent, overriding the above while active |
| **Album-art-derived** | While music is playing, the Island and now-playing tile can temporarily borrow the accent from the album art, then revert when playback stops |

**Accent propagation rules:**
- The accent is stored as a single HSL value; every dependent color (hover state, pressed state, disabled state, gradient stop, glass tint) is derived by adjusting L/S, never re-picked independently. This keeps the whole UI harmonized even as the base hue changes.
- Text/icon contrast against the accent is auto-corrected (WCAG-aware luminance check) so a bright yellow accent doesn't produce unreadable white-on-yellow labels.
- Accent transitions (e.g. switching wallpaper, or the slow time-based rotation) animate as a smooth color interpolation across all UI elements simultaneously (300–800ms crossfade), not an instant hard cut.

### 2.2 Base Palette

| Token | Light | Dark |
|---|---|---|
| `bg.primary` | `#F7F7F8` | `#121212` |
| `bg.surface` | `#FFFFFF` | `#1C1C1E` |
| `bg.surfaceRaised` | `#FFFFFF` + 4dp shadow | `#242426` |
| `text.primary` | `#1A1A1D` | `#F2F2F3` |
| `text.secondary` | `#6B6B70` | `#A0A0A5` |
| `divider` | `#E5E5E7` | `#2C2C2E` |
| `accent` | *dynamic (see 2.1)* | *dynamic* |
| `accent.muted` | accent @ 12% opacity | accent @ 18% opacity |
| `success` | `#34C759` | `#30D158` |
| `warning` | `#FF9F0A` | `#FFB340` |
| `danger` | `#FF3B30` | `#FF453A` |

### 2.3 Elevation & Depth

Rather than pure flat design, Pulse uses **layered depth**: three elevation tiers (surface, raised, floating) distinguished by shadow softness + subtle background luminance shift, not borders. The Island and Control Center live at the "floating" tier — always the visually topmost, softest-shadowed layer, reinforcing that they're temporary overlays, not part of the base workspace.

---

## 3. Typography

### 3.1 Type Families

| Role | Family | Notes |
|---|---|---|
| UI / body | **Inter Variable** | Default for all chrome, labels, settings, feed body text |
| Display (clock, temp, battery %, big numbers) | **Clash Display** or **General Sans** (bold/semibold) | Used sparingly — hero numbers only |
| Monospace (timers, stats, search inline-answers) | **JetBrains Mono** | Countdown timers, calculator results, system stats |
| Optional expressive (user-selectable, feed headlines) | **Playfair Display** (serif) or **Caveat** (handwritten) | Theme packs may swap this in for an editorial or personal feel |

### 3.2 Type Scale

| Token | Size | Weight | Use |
|---|---|---|---|
| `display.xl` | 64sp | 700 | Lock screen clock, Island expanded hero number |
| `display.l` | 40sp | 700 | Home clock widget, weather temp |
| `headline` | 24sp | 600 | Section headers, card titles |
| `title` | 18sp | 600 | Tile labels, list row primary text |
| `body` | 15sp | 400 | Feed copy, settings descriptions |
| `caption` | 13sp | 400 | Timestamps, secondary metadata |
| `micro` | 11sp | 500 | Badges, tags |

Variable font weight is used to build hierarchy *within* a single family wherever possible (e.g. 400→600 for emphasis) rather than mixing many families — keeps the page visually calm even when dense.

---

## 4. Icon Style System

Icon Studio is a **composable style pipeline**: Shape → Style → Size → Label, applied per-icon-pack or globally, with per-app override.

### 4.1 Shapes
Circle, Squircle, Rounded Square, Teardrop, Hexagon, None (native pack shape).

### 4.2 Styles

| Style | Visual description |
|---|---|
| **Glass** | Frosted translucent surface, blurred backdrop showing through, thin light-catching edge highlight along the top border, adjustable transparency/blur radius/reflection angle |
| **Liquid Glass** (flagship) | True refractive glass — icon bends and displaces the background behind it, chromatic-aberration fringe at edges, specular highlight that shifts with a simulated light source, gentle "jiggle" physics on press |
| **Embossed** | Raised, pressed-into-material look; directional top-left light source casts an inner shadow and bottom-right highlight for a tactile, 3D-carved feel |
| **Material** | Flat solid fill, single shape, no additional effect — the calm baseline |
| **Neon** | Dark fill with a glowing colored outline, glow intensity/radius adjustable, accent-color-linked by default |
| **Holographic** | Iridescent hue-shifting surface, slowly animates across the spectrum as light "moves" across the icon |
| **Duotone** | Grayscale base icon, two-tone accent + shadow tint applied |
| **Gradient** | Linear/radial/conic gradient fill, accent-color-linked stops |
| **Film Grain** | Fine noise texture overlay for a matte, analog finish |
| **Liquid Metal** | Chrome/mercury look, moving specular highlight simulating environment reflection |
| **Enamel** | Glossy pin-badge look — solid color, thin white border, elliptical top highlight |
| **Retro Pixel** | Downsampled, nearest-neighbor pixel-art rendering |
| **Wireframe** | Outline-only, edges extracted from the icon's alpha channel |
| **Stacked (2 styles)** | Any two styles composited in sequence (e.g. Holographic + Film Grain) for a signature combined look |

### 4.3 Icon Studio UI Behavior
- Live full-grid preview updates instantly as styles/parameters change.
- Per-app override list below the global style picker — long-press any app in the override list to assign a custom style/shape just for that icon.
- Export/import as a `.pulsetheme` bundle (style + shape + size + per-app overrides + accent + wallpaper reference).

---

## 5. Motion & Animation Language

### 5.1 Motion Principles
1. **Everything has mass.** Motion uses spring physics (stiffness/damping), not linear or simple ease curves, so elements feel like they have weight and settle naturally.
2. **Continuity over cuts.** Where possible, elements *morph* between states (pill → expanded Island) rather than cross-fading between two unrelated layouts.
3. **One thing leads.** Only one major motion happens at a time; secondary elements follow with staggered delay (30–60ms offsets) rather than everything animating simultaneously.

### 5.2 Signature Motions

| Interaction | Motion |
|---|---|
| Slide 1↔2↔3 navigation | Horizontal slide with layered parallax — background moves at 0.7×, foreground content at 1×, Island stays pinned |
| App launch | Icon scales down on press (0.92×), then the app "unfolds" from the icon's position into full screen (shared-element expand) |
| Island compact → expanded | Pill morphs (width + height + corner radius animate together via shared shape interpolation) into the full card, content crossfades in during the last 40% of the motion |
| Island idle state | Subtle breathing animation (2–3% scale pulse, ~4s cycle) when a live activity is active, communicating "alive" without being distracting |
| Tile press | Slight depress (translateY 2dp + shadow reduction) mimicking a physical button |
| Control Center open | Slides down from top-right with an elastic overshoot settle |
| Search overlay open | Scales up from the search-trigger point + blur-in background dim |
| Wallpaper/accent change | Full-UI color crossfade, staggered slightly so the Island updates a beat after the base UI, drawing the eye |
| Notification arrival in Feed | New card slides in from top and gently pushes existing cards down, with a soft highlight flash that fades over 1s |
| Long-press quick actions | Radial arc of 3–4 action icons springs out from the pressed icon, each with a slight stagger |

---

## 6. Layout & Spacing

- **8dp base grid** throughout; card padding 16dp, section gaps 24dp, screen margins 20dp.
- **Corner radii:** small controls 12dp, tiles/cards 20dp, sheets/panels 28dp, Island pill fully rounded (capsule).
- **Touch targets:** minimum 48×48dp, with generous spacing between interactive rows in list views (Slide 3) to preserve the "calm" feel.

---

## 7. Component Library (high-level)

- **Cards** (feed, bento grid) — surface elevation tier "raised," 20dp radius, optional accent-tinted top border for category coding.
- **Tiles** (Slide 2) — 1×1, 1×2, 2×1, 2×2 sizes; icon-driven or live-content-driven (mini chart, media art, progress ring).
- **Pill / Island** — capsule shape, floating elevation, always accent-aware glow.
- **List row** (Slide 3, Settings) — icon + label + optional trailing control, 56dp min height.
- **Sliders** (Icon Studio, Control Center) — accent-colored fill track, floating value label while dragging.
- **Toggles** — capsule switches, accent fill when on.
- **Search bar** — capsule input, leading search icon, trailing mic icon, expands to full overlay on focus.

---

## 8. Theming & Personalization Summary

A **Theme Pack** bundles: accent source + value, icon shape + style + per-app overrides, wallpaper (or wallpaper behavior rule, e.g. time-based), font pairing, animation personality (Subtle / Balanced / Expressive — a single multiplier over spring stiffness/damping/duration across the whole app), and haptic pattern set. Theme Packs are exportable/importable and can be scoped per Focus Mode.

---

*Next docs: `performance.md` (device-tiering strategy for the BF6 and similar hardware), `architecture.md` (services, data flow, island↔app contract), `api-integrations.md` (public-apis wiring for feed/tiles).*
