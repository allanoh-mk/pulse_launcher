# 12 — Roadmap & Tracking

## Phases

### Phase 1 — Skeleton (Weeks 1–3)
- [ ] Fork Lawnchair, set up build
- [ ] 3-slide workspace (basic: empty pages, swipe nav)
- [ ] Icon Studio: shape + size + label + icon pack loading
- [ ] Basic Material You integration
- [ ] Project structure + shared module

### Phase 2 — Visual Identity (Weeks 4–7)
- [ ] Icon styles: Glass, Embossed, Material
- [ ] Icon styles: Neon, Holographic, Duotone, Gradient
- [ ] Preview grid (live)
- [ ] Font system + pairing presets
- [ ] Color system + accent
- [ ] Wallpaper system (static + gradient)
- [ ] Theme presets

### Phase 3 — Daily Driver (Weeks 8–11)
- [ ] Feed (Slide 1): masonry + bento layouts
- [ ] Feed content: notifications, weather, calendar, music
- [ ] Unified Search (all sections)
- [ ] Micro Results (math, units, tracking)
- [ ] Notification architecture (feed vs. island vs. tiles)
- [ ] Basic theming polish

### Phase 4 — Dynamic Island (Weeks 12–15)
- [ ] Island service (overlay, foreground)
- [ ] Compact + Expanded states
- [ ] Music activity
- [ ] Timer / Stopwatch
- [ ] Call activity
- [ ] Charging indicator
- [ ] Spring animations between states
- [ ] Multi-activity handling

### Phase 5 — Tile Grid + Control Center (Weeks 16–18)
- [ ] Tile Grid (Slide 2): layout, resize, pin
- [ ] Tile Dashboards (long-press expand)
- [ ] Smart Tiles (context-aware content)
- [ ] Control Center overlay
- [ ] Control Center styling (matches icon style)

### Phase 6 — Intelligence (Weeks 19–21)
- [ ] Assistant: rule-based commands
- [ ] Assistant: Gemini API integration
- [ ] Assistant: voice input (STT)
- [ ] Assistant: context provider
- [ ] AI-curated feed (time-aware)
- [ ] Quick Actions (long-press radial menu)

### Phase 7 — Polish (Weeks 22–24)
- [ ] Haptic engine (all patterns)
- [ ] Gesture system (all defaults + customization)
- [ ] Animation personality slider
- [ ] Slide transitions (all variants)
- [ ] Shader wallpapers (aurora, fluid, mesh)
- [ ] Widget gallery (10+ custom widgets)
- [ ] Focus modes
- [ ] Export/Import (full config)
- [ ] Bug fixes + performance

### Phase 8 — Stretch (Ongoing)
- [ ] Prismal integration (Tier 2 glass)
- [ ] Island Activity Contract (personal apps)
- [ ] Theme packs (shareable)
- [ ] Voice wake word (Porcupine)
- [ ] Macro system (multi-step gestures)
- [ ] Tablet / foldable layouts
- [ ] More icon styles (Stained Glass, Origami, Blueprint)
- [ ] More feed layouts
- [ ] More shader wallpapers

## Tracking

Use GitHub Issues with labels:
- `phase-1` through `phase-8`
- `icon-studio`, `island`, `feed`, `search`, `control-center`, `assistant`, `theming`, `animation`, `gesture`, `haptics`, `wallpaper`, `fonts`
- `bug`, `feature`, `enhancement`, `performance`

## Definition of Done (Per Feature)

- [ ] Works on target device (test on 2–3 phones)
- [ ] Respects active theme (dark/light, accent, icon style)
- [ ] Has haptic feedback
- [ ] Animates with current animation personality
- [ ] No memory leaks (profile with Android Studio)
- [ ] 60fps (no jank on mid-range device)
- [ ] Documented in this spec
