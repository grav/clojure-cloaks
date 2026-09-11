(require '[fib :as fib])

(assert (= 6765 (fib/naive-fib 20) (fib/tail-fib 20)))
(try
  (fib/tail-fib 20000)
  (println "JVM completed; this JVM's stack is larger than the comparison expects.")
  (System/exit 1)
  (catch StackOverflowError _
    (println "JVM Clojure: StackOverflowError at n=20000 with -Xss256k (expected).")))
