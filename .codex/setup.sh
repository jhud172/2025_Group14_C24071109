#!/bin/bash

echo "Setting up One-to-One Codex worktree environment..."

# install frontend dependencies if package.json exists
if [ -f package.json ]; then
  npm install
fi

# optional frontend build
if [ -f package.json ]; then
  npm run build || true
fi

echo "Codex environment ready."