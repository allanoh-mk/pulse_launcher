# 01 — Home Screen (3 Slides)

## Overview

The home screen is exactly **3 slides**, no more, no less.
Swipe left/right to navigate. Each slide has its own wallpaper,
layout, and content type.

## Slide 1 — Feed

**Inspiration:** Nova Launcher feed + iOS Today View

- Scrollable card-based feed
- Content sources:
  - Notifications (grouped by app, smart grouping)
  - News (RSS / API)
  - Calendar (next 3 events)
  - Weather (current + hourly strip)
  - Music (now playing card)
  - AI-curated content (time-aware: morning = schedule, evening = relaxation)
- Layout options (user-selectable):
  - Masonry (2-column)
  - Bento grid (iOS-style)
  - Horizontal carousels (stacked sections)
  - Card stack (one at a time, swipe up)
  - Timeline (vertical, color-coded dots)
  - Split view (60/40 feed + utility)
  - Magazine (hero + grid)
  - Minimal list (text only)
- Layout can auto-switch by time of day
- Each card respects the active icon style (glass cards, etc.)

## Slide 2 — Tile Grid

**Inspiration:** Mur Launcher & Windows Phone Live Tiles

> ### *Tiles. Reimagined.*
> Live tiles. Press-and-hold dashboards. A grid that bends around your day. The Android home screen, finally worth setting up.
>
> **Your home screen, actually doing things.**
> Tiles aren't icons in boxes. They show what's playing, who messaged, what's next on the calendar. Glance, done. No tap to find out.
>
> **Fit more. Or keep it simple.**
> Small for a quick tap. Big for a whole live dashboard. Every spot on the grid earns what it gets.
>
> **Hold a tile. Open a hub.**
> Long-press and the tile folds open into your stuff. Notifications, a widget, your shortcuts. The app underneath is still one tap away.
>
> **Looks that feel right.**
> Icons, palette, grid density, themes. Lean Metro, full Material You, or somewhere weird in between. Tune it until it actually feels like your phone.
>
> **Why We Built It — Yeah, we loved Windows Phone too.**
> The tile grid felt alive in a way an icon grid never did. You could see things without opening anything. Then it went away. Android got a lot better at most things, but the home screen stayed a wall of stamps. We didn't want to clone Metro. We wanted the feeling back — tiles that show you stuff, a grid that bends — built on top of what Android does well now. Dynamic color. Real widgets. Smooth gestures. So we built one. Not a skin. Not a tribute. Just the launcher Android has been quietly missing. Not retro. Just right for right now.

- Grid of resizable tiles (2×2, 2×4, 4×4, etc.)
- Tile types:
  - App shortcut (icon + label)
  - Live tile (weather, calendar, battery, music, stock)
  - Widget (standard Android AppWidget)
  - Quick action (toggle WiFi, flashlight, etc.)
- Long-press tile → **Tile Dashboard** (expandable overlay):
  - All notifications from that app
  - App shortcuts
  - Embedded widget
  - Quick actions
- Auto-categorization: tiles can be grouped (Social, Work, Media, Tools)
- Pinned sections at top
- Tiles show badge counts (unread messages, etc.)

## Slide 3 — Vertical List

**Inspiration:** Niagara Launcher

- Compact vertical scrolling list of all apps
- Alphabetical, one-handed scroll
- Each row: icon + name (optional subtitle)
- Swipe right on row → app shortcuts (like Niagara pop-ups)
- Long-press → context menu (uninstall, info, quick actions)
- Search bar at top (filters list in real-time)
- Can switch between:
  - Full list (all apps)
  - Favorites only (pinned apps)
  - Recently used
- "Wave Alphabet" — flick to jump to a letter

## Slide Navigation

- Horizontal swipe between slides
- Parallax: wallpaper moves at 0.7× speed during swipe
- Haptic tick on slide change
- Optional: dot indicator at bottom (3 dots)
- Double-tap empty space → jump to specific slide (configurable)

## Per-Slide Configuration

Each slide can have:
- Its own wallpaper (static, animated, shader)
- Its own layout variant
- Its own accent color override (optional)
- Show/hide toggle (user can disable a slide → 2-slide mode)
