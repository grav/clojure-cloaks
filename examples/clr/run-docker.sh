#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
exec docker run --rm --user "$(id -u):$(id -g)" \
  -e DOTNET_CLI_HOME=/tmp/dotnet -e NUGET_PACKAGES=/app/obj/nuget \
  -e DOTNET_NOLOGO=1 -e DOTNET_CLI_TELEMETRY_OPTOUT=1 \
  -v "$PWD:/app" -w /app \
  mcr.microsoft.com/dotnet/sdk:8.0@sha256:5ef85cc12cb25be6ec319a7392d1e9efd53c3bc8abb971c53d8058a473f09053 \
  dotnet run -- "$@"
