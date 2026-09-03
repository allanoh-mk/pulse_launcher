# Codebase Audit Plan

## Correction
Google/Discover-style feed integration is in use by the Feed area. Mark it KEEP/REFACTOR.

## Sequence
1. Gradle module graph.
2. Android manifest graph.
3. Pulse-specific source inventory.
4. Lawnchair feature inventory.
5. Android-version compatibility audit.
6. Tests and build tooling.
7. Dead asset/reference scan.
8. MetroList architecture and license audit.

## Compatibility candidates
compatLibVQ and compatLibVR are candidates because Pulse's minimum is Android 12/API 31. They are NOT approved for deletion until build and runtime references are traced.

## Classification
KEEP, REFACTOR, MIGRATE, OPTIONAL, DELETE CANDIDATE, REMOVE.

## Evidence before deletion
No Gradle dependency, source reference, manifest registration, resource reference, test/CI dependency; then clean build and smoke tests. No deletion based only on folder names.