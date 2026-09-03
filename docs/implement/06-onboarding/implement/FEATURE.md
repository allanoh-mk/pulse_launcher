# Pulse Onboarding — Seven Screens

## First-launch media
The repository contains a Pulse video and image intended for first-launch presentation. Move production assets into a named onboarding/branding location and verify size and licensing before packaging.

## Flow
1. **Welcome to Pulse** — introduce Feed, Bento, and List.
2. **Your phone, your control** — explain customization and local intelligence.
3. **Choose default launcher** — guide the Android default-home role flow.
4. **Notifications** — explain badges and Pulse Reply Cards; denial is supported.
5. **Contacts and files** — explain optional search sources; request access contextually.
6. **Battery reliability** — explain battery restrictions and request optimization exemption only when justified and supported.
7. **Finish** — choose Minimal, Balanced, Creator, Music, Student, or custom starter layout.

## Rules
No permission dump. Every permission has a plain-language purpose. Denial never crashes onboarding. The flow is resumable after process death and versioned for future migrations.

## Motion
Transitions 220–320 ms; illustration motion pauses when system animator scale is zero and respects reduced-motion choices.