# 05 — Unified Search

## Overview

One search bar. Always accessible.
Triggered by: swipe down from top-center, double-tap empty space,
or tap the search bar on Slide 3.

## Architecture

```
User Input
    ↓
[Query Parser]
    ├── Is it a number/math? → Micro Result (instant)
    ├── Is it a unit? → Conversion (instant)
    ├── Is it a tracking number? → Package/Flight lookup
    ├── Is it a known app name? → App result
    ├── Is it a contact? → Contact result
    ├── Is it a calendar event? → Calendar result
    ├── Is it a file? → File result
    ├── Is it a setting? → Settings result
    ├── Is it a web query? → Web search
    └── Is it a natural language query? → Assistant (LLM)
```

## Result Sections (ordered)

1. **Micro Results** (instant, no network)
   - Math: "2+2×3" → 8
   - Unit conversion: "100km to miles" → 62.14 mi
   - Date calculation: "what day is 90 days from now"
   - Package tracking: "1Z999AA10123456784" → tracking status
   - Flight: "UA1234" → flight status

2. **Apps** (local, instant)
   - Fuzzy match on app name
   - Shows icon (with active style) + name
   - Top 5 results

3. **Contacts** (local)
   - Name + phone number
   - Tap → dial / message

4. **Calendar** (local)
   - Upcoming events matching query
   - Tap → open event

5. **Files** (local)
   - File name match
   - Tap → open

6. **Settings** (local)
   - "wifi", "bluetooth", "dark mode" → jump to setting

7. **Web** (network)
   - Top 3 results (Google/Bing API or DuckDuckGo)
   - Title + URL + snippet

8. **Assistant** (LLM)
   - Shown when query is natural language / ambiguous
   - "What should I wear today?" → weather + suggestion
   - "Summarize my notifications" → LLM summary
   - "Set a timer for 25 minutes" → action
   - Shown as a card with "Ask" button (or auto-answer if confident)

## UI

- Full-screen overlay (or 80% height sheet)
- Search bar at top (rounded, glass background)
- Results scroll vertically
- Section headers (sticky)
- Each result: icon + title + subtitle + chevron
- Micro Results: large text, centered, no icon
- Recent searches: shown when search bar is empty
- Pinned/favorite searches: at top
- Keyboard: shows on focus, hides on scroll
- Voice input: mic button in search bar

## Keyboard Shortcuts (Physical Keyboard / Tablet)

- Type → instant results
- Tab → cycle through result sections
- Enter → open top result
- Esc → close
