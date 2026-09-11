# ClojureCLR called from C#

A small C# console host loads [hello.clj](hello.clj) and calls its functions through
ClojureCLR's public API. The Clojure code calls .NET's `String.Format`,
`String.IsNullOrWhiteSpace`, and `DateTimeOffset` APIs. Strings cross the language
boundary as objects, so names are never evaluated as code.

On this VM, add the locally installed .NET SDK 8 to PATH once in your shell:

```sh
export PATH="$HOME/.local/share/dotnet:$PATH"
```

Then build and run directly (or use an existing SDK 8 on PATH):

```sh
cd examples/clr
dotnet run -- Ada
dotnet run -- --check
```

Or use the pinned SDK container (no native .NET installation needed):

```sh
cd examples/clr
./run-docker.sh Ada
./run-docker.sh --check
```

Example output:

```text
Hello, Ada from ClojureCLR! UTC: 2026-09-11T07:49:42.7976703+00:00
```

No argument greets `world`; multiple arguments form one name. `--check` verifies
empty/whitespace names, Unicode, quoted input, newlines, and a parseable UTC
timestamp returned from .NET through ClojureCLR. It exits nonzero on failure.

Verified directly on Linux ARM64 with .NET SDK 8.0.425 and ClojureCLR 1.12.2
on 2026-09-11; all interop checks passed without Docker.
Install SDK 8 using the [official .NET installer](https://learn.microsoft.com/dotnet/core/tools/dotnet-install-script),
or your package manager.
The SDK Docker image is pinned by digest, ClojureCLR by package version, and
transitive NuGet dependencies by `packages.lock.json`. Build outputs and the
container's NuGet cache live in ignored `bin/` and `obj/` directories.

Sources: [official C# embedding guide](https://github.com/clojure/clojure-clr/wiki/Using-ClojureCLR-in-a-C%23-project),
[ClojureCLR package](https://www.nuget.org/packages/Clojure/1.12.2).
