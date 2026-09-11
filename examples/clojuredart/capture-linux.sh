#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
app=build/linux/arm64/release/bundle/cljd_clojuredart
"$app" >build/linux-capture.log 2>&1 &
app_pid=$!
trap 'kill "$app_pid" 2>/dev/null || true' EXIT
window=$(timeout 30 xdotool search --sync --onlyvisible --name 'Hello, everywhere' | head -1)
# Allow the first Flutter frame to finish before capturing the native window.
sleep 2
import -window "$window" screenshot-linux.png
kill -0 "$app_pid"
echo 'Native Linux release window captured in screenshot-linux.png'
