# ⚡ Pulse Launcher

> **A bespoke, high-performance Android launcher built for a deeply personalized home-screen experience.**

Pulse Launcher is an experimental personal Android launcher built on the **Launcher3/Lawnchair architecture** and extended with a modern, highly customizable interface. The project explores a three-slide workspace, modular Bento-style tiles, Niagara-inspired app navigation, advanced theming, universal search, and experimental AI-powered experiences.

## ✨ Vision

Pulse Launcher is designed around one simple idea:

**Your home screen should adapt to you—not force you into a grid.**

The project combines ideas inspired by modern mobile interfaces while building its own identity around:

- ⚡ Speed and responsiveness
- 🎨 Deep visual customization
- 🧩 Modular information dashboards
- 🔎 Fast universal search
- 🌊 Fluid, spring-driven interactions
- 🤖 Optional AI-assisted experiences
- 🔒 Privacy-conscious personal tooling

## 🖥️ Workspace Concept

Pulse Launcher explores a **three-slide home experience**:

### 1. Feed

A glanceable information space designed for important context.

Potential capabilities include:

- Dynamic time and weather
- Upcoming calendar events
- Notification catch-up
- Digital wellbeing information
- Context-aware summaries

### 2. Bento Tiles

A modular dashboard with configurable tile sizes and interactive controls.

Planned concepts include:

- 1×1, 2×1, 2×2 and larger tiles
- Media controls
- Clock and date complications
- Connectivity shortcuts
- Folder dashboards
- Animated flip cards
- Drag-and-drop customization

### 3. App List

A fast, ergonomic application browser inspired by vertical launcher workflows.

Explored features include:

- Alphabetical navigation
- Wave-style fast scrolling
- Search-as-you-type
- Contextual app suggestions
- Hidden and protected app sections

## 🚀 Feature Direction

Pulse Launcher is a large experimental project. The architectural roadmap explores the following areas.

### 🎨 Personalization

- Custom icon packs and per-app icon overrides
- Dynamic icon shapes and masks
- Material You-inspired theming
- Typography and font customization
- Adjustable labels, colors and sizing
- Theme bundles and advanced visual styling

### 🔎 Universal Search

The long-term search architecture is intended to support multiple sources such as:

- Installed applications
- Deep shortcuts
- Contacts
- Files
- Settings
- Web search providers
- Local calculations and conversions

### 🏝️ Dynamic Island Experiments

Pulse explores a compact-to-expanded activity surface for launcher experiences such as:

- Media playback
- Timers and countdowns
- Charging status
- Privacy indicators
- Other live activity concepts

> Some Android system-level behavior depends on platform permissions, device manufacturers, and Android version restrictions.

### 🤖 AI Experiments

The roadmap includes an optional multi-provider AI layer for experiences such as:

- Notification summaries
- Contextual assistance
- Search enhancements
- Provider failover experiments

AI functionality is **experimental** and should not be considered production-ready.

### 🎛️ Control Center

The project also explores a customizable quick-control experience, including concepts for:

- Media controls
- Flashlight access
- Connectivity shortcuts
- Rotation controls
- Audio routing

Android restricts direct control of certain system settings. Implementations should always respect current platform APIs and permission requirements.

## 🏗️ Architecture

Pulse Launcher is based on a mature Android launcher foundation and contains a substantial multi-module codebase.

The project includes components and infrastructure around:

- Java and Kotlin
- Gradle
- Android build tooling
- Launcher3 foundations
- Lawnchair-derived launcher components
- System UI compatibility libraries
- Baseline profile tooling
- Automated tests

The repository is large because it retains significant launcher infrastructure required for Android home-screen and recents integration.

## 📱 Target Platform

The current project roadmap focuses on modern Android hardware, with particular experimentation around:

- **Android 15 / API 35**
- Modern Android launcher APIs
- Dynamic color systems
- Compose-oriented UI modernization

Actual device compatibility depends on the modules and platform APIs used by a particular build.

## 🛠️ Building

### Prerequisites

Before building, you will generally need:

- A compatible JDK
- Android SDK and platform tools
- A Gradle-compatible development environment
- Git with submodule support

### Clone

```bash
git clone --recurse-submodules https://github.com/allanoh-mk/pulse_launcher.git
cd pulse_launcher
```

If you already cloned the repository without submodules:

```bash
git submodule update --init --recursive
```

### Build

This is a complex Android launcher codebase. Available Gradle tasks and build requirements can vary as the project evolves.

Start by inspecting the available tasks:

```bash
./gradlew tasks
```

Then use the task appropriate for the module or build variant you are working on.

> **Tip:** Launcher development can involve device-specific setup and privileged/system integration. A successful Gradle build does not automatically guarantee that every launcher or Quickstep feature will work identically on every device.

## 🧪 Development Status

⚠️ **Experimental / active development**

Pulse Launcher currently contains both inherited launcher infrastructure and custom experimental work.

That means:

- Some features are architectural proposals or work in progress.
- Some roadmap items may not yet be implemented.
- APIs and project structure may change.
- Not every experimental feature is expected to work on all Android devices.

The project is intentionally ambitious. In other words: **the roadmap has entered the group chat before every feature has finished shipping.** 😄

## 🗺️ Roadmap

The broader implementation direction includes:

- [ ] Complete custom workspace experiences
- [ ] Expand Bento tile infrastructure
- [ ] Improve universal multi-source search
- [ ] Build richer feed cards and live information surfaces
- [ ] Continue Compose-based UI modernization
- [ ] Improve icon rendering and customization
- [ ] Expand theming and typography tools
- [ ] Experiment with contextual launcher intelligence
- [ ] Strengthen backup and restore workflows
- [ ] Add comprehensive validation and performance gates

Detailed architectural notes are maintained in:

- `IMPLEMENTATION_PLAN.txt`
- `GATES.md`
- `GITHUB_CHANGELOG.md`
- `TELEGRAM_CHANGELOG.txt`

## 🤝 Contributing

Contributions, experiments, bug reports, and architectural discussions are welcome.

Before contributing, please review:

- `CONTRIBUTING.md`
- `CODE_OF_CONDUCT.md`
- `SECURITY.md`

Because this repository contains a large launcher codebase, please keep changes focused and test them carefully before opening a pull request.

## 📄 License

This repository includes a `LICENSE.txt` file and code originating from multiple upstream components.

**Please review the license and all applicable upstream licensing requirements before redistributing, relicensing, or publishing modified builds.**

## 🙏 Credits

Pulse Launcher builds on the work of the broader Android open-source ecosystem, including the technologies and upstream launcher projects that provide its foundation.

Special appreciation goes to the developers and contributors behind:

- Android Launcher3
- Lawnchair and its contributors
- Android open-source tooling and libraries

## 📌 Repository

**Pulse Launcher**

urlView the repository on GitHubhttps://github.com/allanoh-mk/pulse_launcher

---

### ⚡ Pulse Launcher

**Experimental. Personal. Fast. Built to make the Android home screen feel less like a drawer and more like a control surface.**
