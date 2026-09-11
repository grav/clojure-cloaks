# Glojure in a small OCI image

The greeting is implemented in [hello.glj](hello.glj). A small Go host embeds
that source, invokes its function, and serves the result using `net/http`.
The executable contains the interpreter and script; there is no runtime download.

```sh
cd examples/glojure
docker build -t clojure-hello/glojure:local .
docker run --rm --read-only --cap-drop ALL -p 127.0.0.1:8080:8080 clojure-hello/glojure:local
```

In another terminal:

```sh
curl 'http://localhost:8080/?name=Ada'
# {"language":"Glojure","message":"Hello, Ada from Glojure!"}
curl http://localhost:8080/healthz
# ok
```

Native build (Go 1.24+):

```sh
CGO_ENABLED=0 go build -tags glj_aot_runtime -trimpath -ldflags='-s -w' -o bin/hello .
./bin/hello
```

`ADDR` overrides the default `:8080`. The final OCI image starts from `scratch`,
contains only the stripped, statically linked executable, and runs as UID/GID
65532. It requires no shell, libc, JVM, or Go toolchain. The compact Glojure runtime
build tag removes development features; the script is evaluated at startup,
not AOT-compiled into Go source.

Verified on Linux ARM64, 2026-09-11: native build and OCI build; HTTP health check,
default name, named greeting and Unicode name through the container running with
a read-only filesystem and all capabilities dropped. No image has been published.

Dependency pinned to [Glojure v0.7.16](https://github.com/glojurelang/glojure/tree/v0.7.16)
in go.mod/go.sum. The Go host serializes interpreter entry to avoid assuming
concurrent evaluation is safe in this demonstration.

Run `./smoke.sh` in this directory to repeat the integration checks and clean up
the test containers automatically.
