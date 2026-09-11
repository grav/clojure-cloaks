# Glojure in a small OCI image

The greeting is implemented in [hello.glj](hello.glj). A small Go host embeds
that source, invokes its function, and serves the result using `net/http`.
The executable contains the interpreter and script; there is no runtime download.
Build it locally with Go 1.24+, then package it. Docker only copies the finished
binary into a scratch image; there is no containerized compiler or Alpine stage.
The command targets Linux on the host architecture; use a matching Docker engine.

```sh
cd examples/glojure
CGO_ENABLED=0 GOOS=linux go build -tags glj_aot_runtime -trimpath -o bin/hello .
docker build -t clojure-hello/glojure:local .
docker run --rm -p 127.0.0.1:8080:8080 clojure-hello/glojure:local
```

In another terminal:

```sh
curl 'http://localhost:8080/?name=Ada'
# {"language":"Glojure","message":"Hello, Ada from Glojure!"}
curl http://localhost:8080/healthz
# ok
```

To build and run directly on your host instead:

```sh
CGO_ENABLED=0 go build -tags glj_aot_runtime -trimpath -o bin/hello .
./bin/hello
```

`ADDR` overrides the default `:8080`. The final OCI image starts from `scratch`,
contains only the locally built, statically linked executable, and runs as UID/GID
65532. It requires no shell, libc, JVM, or Go toolchain. The compact Glojure runtime
build tag removes development features; the script is evaluated at startup,
not AOT-compiled into Go source.

Inspect the image and its size:

```sh
# Docker-reported image size, in bytes:
docker image inspect clojure-hello/glojure:local --format '{{.Size}}'
# Unpacked executable size, in bytes:
wc -c < bin/hello
# Layer history: one COPY layer, plus configuration; no Alpine base:
docker history --no-trunc clojure-hello/glojure:local
# Architecture, runtime user, entrypoint, and filesystem layers:
docker image inspect clojure-hello/glojure:local --format 'arch={{.Architecture}} user={{.Config.User}} entrypoint={{json .Config.Entrypoint}} layers={{json .RootFS.Layers}}'
```

Measured on this Linux ARM64 VM after the local build: Docker reports
**11,359,428 bytes (11.36 MB)**; the unpacked executable is
**22,821,120 bytes (22.82 MB)**, also shown by the COPY layer in the history.
Docker's reported size depends on its image store and is not the unpacked
filesystem size. Toolchain and architecture can change these measurements.

Verified on Linux ARM64, 2026-09-11: native build and OCI build; HTTP health check,
default name, named greeting and Unicode name through the container. No image has been published.

Dependency pinned to [Glojure v0.7.16](https://github.com/glojurelang/glojure/tree/v0.7.16)
in go.mod/go.sum. The Go host serializes interpreter entry to avoid assuming
concurrent evaluation is safe in this demonstration.

Run `./smoke.sh` in this directory to repeat the integration checks and clean up
the test containers automatically.
