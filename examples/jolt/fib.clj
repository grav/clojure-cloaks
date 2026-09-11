;; An ordinary tail call: no loop, recur, or trampoline.
(defn fib [n a b]
  (if (zero? n)
    a
    (fib (dec n) b (+' a b))))

(let [n (if-let [arg (first *command-line-args*)] (parse-long arg) 20000)]
  (when-not (and n (<= 0 n 100000))
    (throw (ex-info "Use an integer from 0 to 100000" {})))
  (println "Computing Fibonacci(" n ") with ordinary tail calls")
  (let [result (fib n 0N 1N)]
    (println "digits:" (count (str result)))
    (println "mod-1000000007:" (mod result 1000000007))))
