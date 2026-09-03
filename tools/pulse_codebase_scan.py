#!/usr/bin/env python3
"""Conservative Pulse codebase scanner. It reports candidates; it never deletes files."""
from pathlib import Path
import re
ROOT = Path(__file__).resolve().parents[1]
TEXT_EXT = {'.kt','.java','.xml','.gradle','.kts','.md','.txt','.properties','.json'}
paths = [p for p in ROOT.rglob('*') if p.is_file() and '.git' not in p.parts]
texts = {}
for p in paths:
    if p.suffix.lower() in TEXT_EXT:
        try: texts[p] = p.read_text(errors='ignore')
        except OSError: pass
candidates = []
for p in paths:
    if p.suffix.lower() in {'.mp4','.jpeg','.jpg','.png','.webp','.gif','.svg'}:
        name = p.name
        refs = [q for q,t in texts.items() if q != p and name in t]
        if not refs: candidates.append((str(p.relative_to(ROOT)), 'NO_TEXT_REFERENCE_FOUND'))
print('# Pulse conservative asset scan')
for path, status in candidates: print(f'- {path}: {status}')
print('\nNOTE: Android resources can be referenced by generated IDs or build tooling. Verify before deletion.')