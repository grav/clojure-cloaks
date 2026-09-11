(ns tower
  (:require [sci.core :as sci]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def source-file (io/file (.getParent (io/file *file*)) "interpreter.clj"))
(def interpreter (edn/read-string (slurp source-file)))
(def primitive-names
  '[symbol? seq? vector? first rest empty? nth cons vec get contains? assoc
    = count apply fn? hash-map ex-info + - * <= str])

;; Build a language expression that captures only the primitive functions.
;; Each interpreted layer constructs its own environment from these bindings.
(def environment-form
  (cons 'hash-map (mapcat (fn [name] [(list 'quote name) name]) primitive-names)))

(defn wrap [program]
  (list 'let ['interpret interpreter]
        (list 'interpret (list 'quote program) environment-form)))

(defn run [depth program]
  (when-not (and (integer? depth) (<= 0 depth))
    (throw (ex-info "Depth must be a nonnegative integer" {:depth depth})))
  ;; Exactly one SCI invocation. Everything nested inside is interpreted source.
  (sci/eval-string (pr-str (nth (iterate wrap program) depth))))

(def hello '(let [greet (fn [name] (str "Hello, " name "!"))]
              (greet "world")))

(defn -main [& [depth-arg]]
  (let [depth (if depth-arg
                (or (parse-long depth-arg)
                    (throw (ex-info "Depth must be an integer" {:argument depth-arg})))
                2)
        start (System/nanoTime)
        result (run depth hello)
        ms (/ (- (System/nanoTime) start) 1e6)]
    (println (str "SCI → " depth " mini-interpreter layer(s)"))
    (prn result)
    (printf "Elapsed: %.1f ms%n" ms)))
