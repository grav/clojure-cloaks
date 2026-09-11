#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if command -v dotnet >/dev/null 2>&1; then
  sdk=$(command -v dotnet)
else
  sdk=${DOTNET_ROOT:-$HOME/.local/share/dotnet}/dotnet
fi
if [[ ! -x $sdk ]]; then
  echo 'Install .NET SDK 8 or set DOTNET_ROOT; see README.md.' >&2
  exit 1
fi
export DOTNET_NOLOGO=1 DOTNET_CLI_TELEMETRY_OPTOUT=1
exec "$sdk" run -- "$@"
