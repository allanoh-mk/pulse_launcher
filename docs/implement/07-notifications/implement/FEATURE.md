# Notifications and Pulse Reply Cards

## Intent
Pulse List exposes glanceable notification affordances. A chevron means actionable notification content is available.

## Pulse Reply Card
Tapping the chevron opens a floating launcher-owned rectangle containing:
- title and content allowed by Android APIs
- reply input when RemoteInput exists
- provided actions
- reactions only when exposed by the originating notification
- open conversation/app

Pulse must not fabricate app actions Android did not expose.

## Pipeline
Notification listener → normalized event → badge model → app row → reply card.

## Privacy
Notification previews are independently configurable.

## Failure behavior
If notification access is revoked, badges disappear gracefully and settings explains restoration.

## Tests
RemoteInput reply, multiple notifications per app, sensitive content hidden, listener restart.