(ns hello.server
  (:require [clojure.string :as str]
            [clojure.core.server :as repl])
  (:import [com.sun.net.httpserver HttpServer HttpHandler]
           [java.net InetSocketAddress URLDecoder]
           [java.nio.charset StandardCharsets]
           [java.time Instant]
           [java.util.concurrent Executors]))

(defn greeting [name] (str "Hello, " name " from the JVM!"))

(defn query-name [query]
  (let [params (into {} (for [part (str/split (or query "") #"&")
                            :let [[k v] (str/split part #"=" 2)]]
                        [(URLDecoder/decode k "UTF-8")
                         (URLDecoder/decode (or v "") "UTF-8")]))]
    (if (str/blank? (get params "name")) "world" (get params "name"))))

(defn start! [port]
  (let [requests (atom 0)
        pool (Executors/newFixedThreadPool 4)
        server (HttpServer/create (InetSocketAddress. "127.0.0.1" port) 0)]
    (.createContext server "/"
      (reify HttpHandler
        (handle [_ exchange]
          (with-open [exchange exchange]
            (let [[status response]
                  (cond
                    (not= "GET" (.getRequestMethod exchange)) [405 {:error "Use GET"}]
                    (not= "/" (.getPath (.getRequestURI exchange))) [404 {:error "Not found"}]
                    :else
                    (try
                      [200 {:message (greeting (query-name (.getRawQuery (.getRequestURI exchange))))
                            :request (swap! requests inc)
                            :time (str (Instant/now))}]
                      (catch IllegalArgumentException _ [400 {:error "Malformed query encoding"}])))
                  body (.getBytes (pr-str response) StandardCharsets/UTF_8)]
              (.set (.getResponseHeaders exchange) "Content-Type" "application/edn; charset=utf-8")
              (.sendResponseHeaders exchange status (alength body))
              (with-open [out (.getResponseBody exchange)] (.write out body)))))))
    (.setExecutor server pool)
    (.start server)
    {:port (.getPort (.getAddress server))
     :stop #(do (.stop server 0) (.shutdownNow pool))}))

(defn -main [& [http-port repl-port]]
  (let [app (start! (parse-long (or http-port "8082")))
        socket (repl/start-server {:name "hello" :address "127.0.0.1"
                                  :port (parse-long (or repl-port "5555"))
                                  :accept 'clojure.core.server/repl})]
    (.addShutdownHook (Runtime/getRuntime)
      (Thread. #(do ((:stop app)) (repl/stop-server "hello"))))
    (println (str "HTTP: http://127.0.0.1:" (:port app)))
    (println (str "Socket REPL: 127.0.0.1:" (.getLocalPort socket)))))
