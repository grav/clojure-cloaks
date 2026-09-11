(require '[fib :as fib])

(let [arg (first *command-line-args*)
      n (if arg (parse-long arg) 20000)]
  (when-not (and n (<= 0 n 100000))
    (throw (ex-info "Use an integer from 0 to 100000" {:argument arg})))
  (println "Hello, world from Jolt!")
  (println "Naive Fibonacci(20):" (fib/naive-fib 20))
  (println "Tail-call Fibonacci(20):" (fib/tail-fib 20))
  (let [result (fib/tail-fib n)
        digits (str result)]
    (println "Ordinary tail calls; no loop/recur.")
    (println "n:" n)
    (println "digits:" (count digits))
    (println "first:" (subs digits 0 (min 30 (count digits))))
    (println "last:" (subs digits (max 0 (- (count digits) 30))))
    (println "mod-1000000007:" (mod result 1000000007))))
