# ⚡ Pulse Launcher

> **A personal, adaptive Android launcher built around three distinct home experiences.**

Pulse Launcher is built on Launcher3/Lawnchair foundations and is being reshaped into a deeply personal launcher with exactly three primary screens: **Feed, Bento, and List**.

## 📱 Platform target

- **Minimum supported version: Android 12 / API 31**
- **Android 12 Go is a first-class target**
- Pulse remains **one product on every supported device**—not a separate stripped-down Go edition.
- Internal scheduling and rendering may adapt to available resources, battery mode, and user preferences without removing the user's configured experience.

A real low-memory validation target is the **Tecno Pop 7 BF6**.

## 🖥️ The three screens

### 1. Pulse Feed
A highly customizable information surface integrating schedules, weather, small statistics such as connectivity speed, curated search results, and user-selected feed providers.

Existing Google/Discover-style feed integration is considered an active dependency of this area and must be preserved or refactored deliberately.

### 2. Pulse Bento
A vertically scrollable personal widget world with a polished default layout. Users control widgets, live/static behavior, blur, curves, placement, interactions, and appearance.

### 3. Pulse List
A favorites-first focused launcher experience inspired by Niagara's interaction model, with a configurable top card, vertical app navigation, shared multi-source search, and interactive notification affordances.

## 🔎 Pulse Search
One engine powers drawer search and Screen 3 icon search.

Planned sources include apps, shortcuts, Pulse Music, contacts, user-authorized files, calculator functions, live currency conversion, settings, internet providers, and Feed providers.

## 🧠 Pulse Intelligence
Pulse uses a lightweight local behavioral graph to connect context and actions for app ranking, Feed ordering, search ranking, and recommendations. It is privacy-controlled and does not require a permanently running cloud AI.

## 🎵 Pulse Music
Pulse Music is designed as an integrated immersive music surface rather than a conventional separately launched app. MetroList is a research reference and licensing review is mandatory before any code integration.

## 📚 Implementation documentation

The active Pulse specifications live in:

`docs/implement/`

Each feature has its own implementation folder covering intent, architecture, implementation stages, permissions, failure modes, tests, performance budgets, and motion.

Start at:

`docs/implement/README.md`

## 🛠️ Building

```bash
git clone --recurse-submodules https://github.com/allanoh-mk/pulse_launcher.git
cd pulse_launcher
./gradlew tasks
```

Launcher development can involve Android-version and OEM-specific behavior. A successful Gradle build does not guarantee identical Quickstep or system-integration behavior on every device.

## 🧪 Status

⚠️ **Active experimental development**

Pulse currently contains inherited launcher infrastructure and Pulse-specific work in progress. The implementation documents are the source of truth for planned migration and feature completion.

## 📄 License

The repository contains code originating from multiple upstream components. Review `LICENSE.txt` and all applicable upstream licenses before redistribution or relicensing.

---

**Pulse Launcher — Three interfaces. One adaptive home.**
