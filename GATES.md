# Gates: Pulse Launcher foundation and Phase 1 skeleton

OWNS: GATES.md, scripts/verify-phase1.mjs, app/**, build.gradle.kts, settings.gradle.kts, gradle/**, gradlew, gradlew.bat, gradle.properties, README.md, LICENSE, .gitignore

Scope: Preserve the Pulse design materials, replace the prototype with an Apache-2.0 Lawnchair foundation, and add the documented three-slide skeleton.

- [x] G0: this ledger has runnable, meaningful acceptance checks
  CHECK: node /home/nana/.agents/skills/unlazy/scripts/gate-lint.mjs GATES.md
  EXPECT: LINT OK
  EVIDENCE: exit=0; shell=/bin/sh; cwd=/home/nana/Documents/pulse_launcher; path=9d19879525a2/16 entries; EXPECT=matched; output-sha256=48630b7361dd44ee870917b12c3d19b9d7bdea738aaca16bb04d4cab83b772d2; output-bytes=8

- [x] G1: all Pulse documentation and design images survive the replacement
  CHECK: node scripts/verify-phase1.mjs preserved-materials
  EXPECT: preserved materials verification passed
  EVIDENCE: exit=0; shell=/bin/sh; cwd=/home/nana/Documents/pulse_launcher; path=9d19879525a2/16 entries; EXPECT=matched; output-sha256=2bc48bed9b5b3a6611f6d625015c853cbdc67c04d9d74854891dbce44e695a20; output-bytes=40

- [x] G2: the repository is based on Lawnchair rather than the discarded prototype
  CHECK: node scripts/verify-phase1.mjs lawnchair-base
  EXPECT: Lawnchair base verification passed
  EVIDENCE: exit=0; shell=/bin/sh; cwd=/home/nana/Documents/pulse_launcher; path=9d19879525a2/16 entries; EXPECT=matched; output-sha256=49d28ac62a97517ce78969ddcd881d02f712a50ed563cfa6e464e601f1041277; output-bytes=35

- [x] G3: the Pulse workspace skeleton declares Feed, Tiles, and List pages with swipe navigation
  CHECK: node scripts/verify-phase1.mjs pulse-skeleton
  EXPECT: Pulse skeleton verification passed
  EVIDENCE: exit=0; shell=/bin/sh; cwd=/home/nana/Documents/pulse_launcher; path=9d19879525a2/16 entries; EXPECT=matched; output-sha256=3a3207e7f45ac4e4329a5e75e5ca8998871dbad47cfb9de055d44055b3c445f3; output-bytes=35

- [ ] G4: the Android debug variant compiles from the rebuilt repository
  CHECK: ./gradlew assembleDebug --console=plain
  EXPECT: BUILD SUCCESSFUL
  EVIDENCE: pending
