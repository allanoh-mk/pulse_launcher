---
name: Pulse Launcher
colors:
  surface: '#131313'
  surface-dim: '#131313'
  surface-bright: '#393939'
  surface-container-lowest: '#0e0e0e'
  surface-container-low: '#1c1b1b'
  surface-container: '#201f1f'
  surface-container-high: '#2a2a2a'
  surface-container-highest: '#353534'
  on-surface: '#e5e2e1'
  on-surface-variant: '#c2c6d5'
  inverse-surface: '#e5e2e1'
  inverse-on-surface: '#313030'
  outline: '#8c909f'
  outline-variant: '#414753'
  surface-tint: '#acc7ff'
  primary: '#acc7ff'
  on-primary: '#002f67'
  primary-container: '#4a90ff'
  on-primary-container: '#00295c'
  inverse-primary: '#005bbe'
  secondary: '#c6c6cb'
  on-secondary: '#2f3034'
  secondary-container: '#48494d'
  on-secondary-container: '#b8b8bd'
  tertiary: '#ffb77b'
  on-tertiary: '#4d2700'
  tertiary-container: '#db7900'
  on-tertiary-container: '#452200'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#d7e2ff'
  primary-fixed-dim: '#acc7ff'
  on-primary-fixed: '#001a40'
  on-primary-fixed-variant: '#004491'
  secondary-fixed: '#e3e2e7'
  secondary-fixed-dim: '#c6c6cb'
  on-secondary-fixed: '#1a1b1f'
  on-secondary-fixed-variant: '#46474b'
  tertiary-fixed: '#ffdcc2'
  tertiary-fixed-dim: '#ffb77b'
  on-tertiary-fixed: '#2e1500'
  on-tertiary-fixed-variant: '#6d3900'
  background: '#131313'
  on-background: '#e5e2e1'
  surface-variant: '#353534'
  surface-base: '#1C1C1E'
  surface-raised: '#242426'
  surface-high: '#2E2E32'
  glass-fill: rgba(36, 36, 38, 0.78)
  glass-specular: rgba(255, 255, 255, 0.28)
  accent-glow: rgba(74, 144, 255, 0.24)
  success: '#30D158'
  warning: '#FFB340'
  danger: '#FF453A'
typography:
  display-xl:
    fontFamily: PlusJakartaSans
    fontSize: 64px
    fontWeight: '700'
    lineHeight: 72px
    letterSpacing: -0.03em
  display-lg:
    fontFamily: PlusJakartaSans
    fontSize: 40px
    fontWeight: '700'
    lineHeight: 48px
    letterSpacing: -0.02em
  headline:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: -0.01em
  title:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
    letterSpacing: '0'
  body:
    fontFamily: Inter
    fontSize: 15px
    fontWeight: '400'
    lineHeight: 22px
    letterSpacing: '0'
  caption:
    fontFamily: Inter
    fontSize: 13px
    fontWeight: '400'
    lineHeight: 18px
    letterSpacing: 0.01em
  micro:
    fontFamily: Inter
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 14px
    letterSpacing: 0.02em
  stats-mono:
    fontFamily: JetBrains Mono
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
    letterSpacing: -0.01em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  unit: 8px
  margin-screen: 20px
  gutter-bento: 16px
  padding-card: 16px
  touch-target-min: 48px
  gap-section: 24px
---

## Brand & Style

The design system is anchored in the "Calm, Glanceable, Physical" philosophy. It treats the Android home screen as a tactile operating layer rather than a flat grid. The personality is refined and high-performance, drawing inspiration from the structural discipline of Niagara and the material depth of iOS.

The visual style is a hybrid of **Minimalism** and **Glassmorphism**, specifically utilizing a "Liquid Glass" metaphor. Surfaces should feel like they possess physical weight and refractive properties, creating a sense of hierarchy through depth rather than visual noise. The experience is designed to be unobtrusive, prioritising information density and one-handed ergonomics on a 6.6" canvas.

## Colors

The palette is built on a deep obsidian foundation to maximize contrast and reduce eye strain. The primary "Electric Blue" acts as a high-clarity interactive lead, appearing in buttons, progress indicators, and glass glows.

A single HSL-based dynamic accent engine governs the UI. While the baseline is Electric Blue, the system must support algorithmic adjustments for lightness and saturation to derive states (hover, muted tints, specular glows). In the "Liquid Glass" components, colors should interact with background blurs, appearing as refractive tints rather than solid fills.

## Typography

Typography is used to build hierarchy within a single family where possible, minimizing visual clutter. 

- **Display levels** use **PlusJakartaSans** (as a high-quality alternative to Clash/General Sans) for geometric impact on hero numbers like clocks and temperatures.
- **UI and Body levels** rely on **Inter** for its neutral, systematic legibility.
- **Label levels** for technical data and timers utilize **JetBrains Mono** to maintain character alignment and a "telemetry" feel.

On mobile, use `display-lg` for primary widgets and reserve `display-xl` for lock-screen configurations or high-impact hero moments within the Dynamic Island.

## Layout & Spacing

This design system employs a **fixed grid** approach optimized for 6.6" displays, emphasizing one-handed reachability. The spatial rhythm is strictly 8dp.

- **Screen Margins:** 20dp lateral margins ensure content does not bleed into the bezel or interfere with edge-swipe gestures.
- **Bento Grid:** Use a 16dp gutter between cards. Tiles should follow standard 1x1, 1x2, 2x1, and 2x2 spans.
- **Safe Areas:** Navigation elements and the Dynamic Island must respect top and bottom system insets (approx. 16dp and 24dp respectively).
- **Density:** Maintain a 56dp minimum height for list rows to ensure glanceability and touch accuracy.

## Elevation & Depth

Visual hierarchy is conveyed through **Tonal Layers** and **Glassmorphism**. Pulse Launcher avoids heavy shadows in favor of ambient occlusion and material shifts.

1.  **Tier 0 (Canvas):** Flat #121212 background.
2.  **Tier 1 (Surface):** #1C1C1E with a subtle 1px #2C2C2E outline. Used for passive feed items.
3.  **Tier 2 (Raised):** #242426 with a soft 4dp ambient shadow. Used for interactive bento tiles.
4.  **Tier 3 (Floating/Liquid Glass):** Semi-transparent surfaces (75-85% alpha) with 24px-32px backdrop blur and a 1px specular top highlight. Used for temporary overlays and the status pill.

Interactive elements should respond to touch with a physical depress (2dp downward translation) and a corresponding reduction in shadow intensity to simulate physical compression.

## Shapes

The shape language is dominated by high-radius squircles and pills to reinforce the "liquid" theme. 

- **Standard Tiles/Cards:** Use a 20dp radius (`rounded-lg`) for a soft but structured appearance.
- **Small Controls:** Use 12dp for buttons and inputs.
- **Modal Sheets:** Use 28dp (`rounded-xl`) at the top corners to emphasize their "layered" nature.
- **Dynamic Island:** Always use a fully rounded capsule (`rounded-full`).

Icon shapes are composable; however, the squircle is the flagship recommendation to match the OS aesthetic.

## Components

### Buttons & Toggles
- **Primary Action:** Capsule shape with Electric Blue fill. 
- **Toggles:** 52x32dp capsule switches. Use neutral `#2C2C2E` for 'off' and `#4A90FF` for 'on', utilizing spring motion for the thumb transition.

### Liquid Glass Pill (Dynamic Island)
The flagship component. It is a capsule-shaped floating container. It must feature a 1px white specular highlight on the top edge and an ambient accent-tinted glow when active. It morphs height and width using physical spring constants (`stiffness: 300, damping: 28`).

### Bento Tiles
Tiles are the primary workspace unit. They feature a `surface-raised` background and a 20dp corner radius. For category coding, an optional 2dp top border stroke in the accent color can be applied.

### Input Fields (Search)
A 52dp height capsule. On focus, the background dim increases and the glass blur intensifies, visually separating the input from the workspace.

### Icon Studio pipeline
Icons are treated as 3D objects. The "Liquid Glass" style requires a refractive shader that distorts the background and adds chromatic aberration at the edges. The "Neon" style uses a dark fill with a 2px glowing perimeter linked to the system accent.