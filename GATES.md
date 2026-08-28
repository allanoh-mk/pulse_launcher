# Gates: Pulse Launcher foundation and Phase 1 skeleton

OWNS: GATES.md, scripts/verify-phase1.mjs, app/**, build.gradle.kts, settings.gradle.kts, gradle/**, gradlew, gradlew.bat, gradle.properties, README.md, LICENSE, .gitignore

Scope: Preserve the Pulse design materials, replace the prototype with an Apache-2.0 Lawnchair foundation, and add the documented three-slide skeleton.

- [ ] G0: this ledger has runnable, meaningful acceptance checks
  CHECK: node /home/nana/.agents/skills/unlazy/scripts/gate-lint.mjs GATES.md
  EXPECT: LINT OK
  EVIDENCE: pending

- [ ] G1: all Pulse documentation and design images survive the replacement
  CHECK: node scripts/verify-phase1.mjs preserved-materials
  EXPECT: preserved materials verification passed
  EVIDENCE: pending

- [ ] G2: the repository is based on Lawnchair rather than the discarded prototype
  CHECK: node scripts/verify-phase1.mjs lawnchair-base
  EXPECT: Lawnchair base verification passed
  EVIDENCE: pending

- [ ] G3: the Pulse workspace skeleton declares Feed, Tiles, and List pages with swipe navigation
  CHECK: node scripts/verify-phase1.mjs pulse-skeleton
  EXPECT: Pulse skeleton verification passed
  EVIDENCE: pending

- [ ] G4: the Android debug variant compiles from the rebuilt repository
  CHECK: ./gradlew assembleDebug --console=plain
  EXPECT: BUILD SUCCESSFUL
  EVIDENCE: pending
