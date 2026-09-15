# Clojerl: two-node actor greeting

A client BEAM node sends correlated requests to a registered actor on a separate
BEAM node. The actor owns a request counter and replies with its node name.
Docker Compose supplies the private network and a shared demo cookie.
The cookie is **Erlang’s shared secret for connecting nodes**.

```sh
cd examples/clojerl
docker compose up --build --abort-on-container-exit --exit-code-from client
docker compose down
```

**OTP 26 is version 26 of Erlang/OTP**, the platform Clojerl runs on.

Verified on Linux ARM64 with OTP 26 and Clojerl commit
`4ad14e57df85d30cc3332308f861f4e174383483`:

```text
Greeter ready on :greeter@greeter
Hello, Ada from :greeter@greeter! Request #1
Hello, world from :greeter@greeter! Request #2
Two-node greeting exchange complete.
```

The client waits for actor registration and times out if a reply never arrives.
Compose stops both nodes when the client exits. No host ports are published.
The fixed cookie is for this local demonstration only.

Run `./smoke.sh` in this directory to repeat the integration checks and clean up
the test containers automatically.
