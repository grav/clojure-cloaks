package main

import (
	_ "embed"
	"encoding/json"
	"log"
	"net/http"
	"os"
	"sync"
	"time"

	"github.com/glojurelang/glojure/pkg/glj"
	"github.com/glojurelang/glojure/pkg/runtime"
)

//go:embed hello.glj
var program string

func handler() http.Handler {
	runtime.ReadEval(program)
	greet := glj.Var("hello", "response")
	// Serialize interpreter entry; HTTP handling still uses net/http's
	// goroutines. This demo makes no assumptions about evaluator concurrency.
	var mu sync.Mutex
	mux := http.NewServeMux()
	mux.HandleFunc("GET /healthz", func(w http.ResponseWriter, r *http.Request) {
		w.Write([]byte("ok\n"))
	})
	mux.HandleFunc("GET /{$}", func(w http.ResponseWriter, r *http.Request) {
		mu.Lock()
		message := greet.Invoke(r.URL.Query().Get("name"))
		mu.Unlock()
		w.Header().Set("Content-Type", "application/json; charset=utf-8")
		json.NewEncoder(w).Encode(map[string]any{"message": message, "language": "Glojure"})
	})
	return mux
}

func main() {
	addr := os.Getenv("ADDR")
	if addr == "" {
		addr = ":8080"
	}
	server := &http.Server{Addr: addr, Handler: handler(), ReadHeaderTimeout: 5 * time.Second}
	log.Printf("Hello service listening on %s", addr)
	log.Fatal(server.ListenAndServe())
}
