(ns hello.server-test
  (:require [hello.server :as hello]
            [clojure.core.server :as repl]
            [clojure.edn :as edn]
            [clojure.test :refer [deftest is run-tests]])
  (:import [java.net URI Socket]
           [java.net.http HttpClient HttpRequest HttpResponse$BodyHandlers]
           [java.io BufferedReader InputStreamReader PrintWriter]
           [java.time Instant]))

(deftest http-and-live-redefinition
  (let [app (hello/start! 0)
        listener (repl/start-server {:name "hello-test" :address "127.0.0.1"
                                    :port 0 :accept 'clojure.core.server/repl})
        client (HttpClient/newHttpClient)
        get! (fn [path]
               (let [response (.send client
                                (.build (HttpRequest/newBuilder (URI/create (str "http://127.0.0.1:" (:port app) path))))
                                (HttpResponse$BodyHandlers/ofString))]
                 [(.statusCode response) (edn/read-string (.body response))]))
        original hello/greeting]
    (try
      (let [[status body] (get! "/")]
        (is (= 200 status))
        (is (= "Hello, world from the JVM!" (:message body)))
        (is (= 1 (:request body)))
        (is (instance? Instant (Instant/parse (:time body)))))
      (is (= "Hello, Ægir from the JVM!" (:message (second (get! "/?name=%C3%86gir")))))
      (is (= 404 (first (get! "/missing"))))
      ;; Redefine through an actual socket REPL while the same HTTP service runs.
      (with-open [socket (Socket. "127.0.0.1" (.getLocalPort listener))
                  in (BufferedReader. (InputStreamReader. (.getInputStream socket)))
                  out (PrintWriter. (.getOutputStream socket) true)]
        (.setSoTimeout socket 5000)
        (.println out "(do (alter-var-root #'hello.server/greeting (constantly (fn [n] (str \"Ahoy, \" n \"!\")))) (println \"RELOADED\"))")
        (loop []
          (let [line (.readLine in)]
            (when-not line (throw (ex-info "REPL closed before acknowledging reload" {})))
            (when-not (.contains line "RELOADED") (recur)))))
      (let [[status body] (get! "/?name=Ada")]
        (is (= 200 status))
        (is (= "Ahoy, Ada!" (:message body)))
        (is (= 3 (:request body))))
      (finally
        (alter-var-root #'hello/greeting (constantly original))
        ((:stop app))
        (repl/stop-server "hello-test")))))

(defn -main [& _]
  (let [result (run-tests 'hello.server-test)]
    (shutdown-agents)
    (System/exit (if (zero? (+ (:fail result) (:error result))) 0 1))))
