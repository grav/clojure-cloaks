# Clojure on the JVM: HTTP and a live REPL

A long-lived HTTP service uses the JDK's `HttpServer` and `java.time.Instant`.
An atom counts successful greeting requests. Each request calls a Clojure Var,
so redefining the greeting through the REPL changes the running service while
preserving its counter.

Requires a JDK (11+) and the Clojure CLI:

```sh
cd examples/jvm
clojure -M:run
```

In another terminal:

```sh
curl 'http://127.0.0.1:8082/?name=Ada'
# {:message "Hello, Ada from the JVM!", :request 1, :time "..."}
```

Responses are UTF-8 EDN. An absent or blank name greets `world`. Both the HTTP
server and socket REPL bind to loopback. Optional arguments select their ports:
`clojure -M:run 8082 5555`. Stop with Ctrl-C.

Connect to the socket REPL with netcat (`nc`):

```sh
nc 127.0.0.1 5555
```

Evaluate:

```clojure
(in-ns 'hello.server)
(defn greeting [name] (str "Ahoy, " name "!"))
```

Repeat the curl request. It now returns `Ahoy, Ada!` and the next request number;
the process has stayed alive. HTTP workers share the atom safely. Changes made
in the REPL last until restart; edit `src/hello/server.clj` to keep them.

```sh
clojure -M:test
```

Verified on Linux ARM64, OpenJDK 26.0.2.1 and Clojure 1.12.6 on 2026-09-11:
nine integration assertions passed. The test starts real HTTP and socket REPL
listeners on ephemeral ports, checks default and Unicode greetings, the Java
timestamp and a missing route, redefines the greeting over the socket, then
confirms the new response and preserved counter. It stops both listeners.

Source: [Clojure socket server](https://clojure.org/reference/repl_and_main#_launching_a_socket_server).
