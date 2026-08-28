# 10 — Digital Assistant

## Overview

A built-in AI assistant accessible from:
- Dynamic Island (tap mic / long-press)
- Search bar (natural language queries)
- Voice wake word (optional)
- Control Center (quick action)

## Backends

| Backend | Use Case | Latency |
|---------|----------|---------|
| **Gemini API** (cloud) | Complex queries, creative tasks, multi-step | 1–3s |
| **Ollama** (local) | Simple queries, offline, privacy | 2–5s |
| **Rule-based** (local) | Commands, quick actions, no AI needed | <100ms |

**Routing logic:**
1. Is it a known command? → Rule-based (instant)
2. Is it a simple factual query? → Local model
3. Is it complex / creative / multi-step? → Gemini API
4. Is the user offline? → Local model (fallback)

## Capabilities

### Information
- "What's the weather?" → Weather data
- "What's on my calendar tomorrow?" → Calendar
- "Who called me today?" → Call log
- "Summarize my notifications" → NLP summary
- "What's that song?" → Shazam API / audio recognition

### Actions
- "Set a timer for 25 minutes" → Timer (shows in island)
- "Turn on dark mode" → Settings
- "Play my workout playlist" → Spotify / Music app
- "Navigate to home" → Maps
- "Send 'on my way' to John" → SMS / Messenger
- "Remind me to call mom at 5pm" → Alarm / Calendar
- "Take a screenshot" → Screenshot
- "Start a pomodoro" → Timer + Focus mode

### Creative
- "Write a text to my boss saying I'll be 10 min late"
- "Suggest 3 restaurants near me for dinner"
- "What should I watch tonight?" → Based on preferences
- "Summarize this article" → (from feed / web)

### Context-Aware
The assistant knows:
- Current time + day
- Location (if permitted)
- Foreground app
- Currently playing media
- Pending notifications (count + apps)
- Battery level
- Active focus mode
- Recent actions (conversation history)

This context is passed as a system prompt to the LLM.

## UI

### In Island (Expanded)
- Chat bubbles
- Quick suggestion chips (contextual)
- Mic button + text input
- "Stop" button (for voice)
- Max 10 messages visible

### In Search
- Appears as the last result section
- "Ask Pulse" card with the query pre-filled
- Tap → full assistant view

### Standalone
- Full-screen chat (if launched from control center or gesture)
- Larger font, more context
- Can show rich cards (weather, calendar, music) inline

## Privacy

- All conversations stored locally (SQLite)
- Cloud queries (Gemini) are optional (toggle in settings)
- "Offline mode" → only local model + rule-based
- Conversation history: viewable, deletable, auto-purge (30 days)
- No data leaves device in offline mode
