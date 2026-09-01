import { existsSync, readFileSync, readdirSync } from 'node:fs';
import { join } from 'node:path';

const root = process.cwd();
const mode = process.argv[2];

function assert(condition, message) {
  if (!condition) {
    throw new Error(message);
  }
}

function listFiles(directory) {
  return readdirSync(directory, { withFileTypes: true }).flatMap((entry) => {
    const path = join(directory, entry.name);
    return entry.isDirectory() ? listFiles(path) : [path];
  });
}

if (mode === 'preserved-materials') {
  const docs = join(root, 'docs');
  const extracted = join(root, 'extracted');
  assert(existsSync(docs), 'docs directory is missing');
  assert(existsSync(extracted), 'extracted design directory is missing');
  assert(listFiles(docs).some((file) => file.endsWith('.md')), 'no design markdown remains');
  assert(listFiles(extracted).some((file) => /\.(png|jpg|jpeg|webp)$/i.test(file)), 'no extracted design image remains');
  console.log('preserved materials verification passed');
} else if (mode === 'lawnchair-base') {
  const readme = readFileSync(join(root, 'README.md'), 'utf8');
  const license = readFileSync(join(root, 'LICENSE.txt'), 'utf8');
  assert(/Lawnchair/i.test(readme), 'README does not identify Lawnchair');
  assert(/Apache License/i.test(license), 'LICENSE is not Apache License');
  assert(existsSync(join(root, 'quickstep')), 'Lawnchair Quickstep source is missing');
  console.log('Lawnchair base verification passed');
} else if (mode === 'pulse-skeleton') {
  const workspace = join(root, 'lawnchair/src/app/lawnchair/pulse/workspace');
  const files = ['WorkspaceController.kt', 'FeedPage.kt', 'TileGridPage.kt', 'ListPage.kt'];
  for (const file of files) assert(existsSync(join(workspace, file)), `${file} is missing`);
  const source = files
    .map((file) => readFileSync(join(workspace, file), 'utf8'))
    .join('\n');
  for (const marker of ['Feed', 'Tiles', 'List', 'HorizontalPager']) {
    assert(source.includes(marker), `Pulse workspace is missing ${marker}`);
  }
  console.log('Pulse skeleton verification passed');
} else {
  throw new Error('usage: node scripts/verify-phase1.mjs <preserved-materials|lawnchair-base|pulse-skeleton>');
}
