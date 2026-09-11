(ns fib)

;; Ordinary function call in tail position: no loop, recur, or trampoline.
(defn fib-step [n a b]
  (if (zero? n)
    a
    (fib-step (dec n) b (+' a b))))

(defn tail-fib [n]
  (fib-step n 0N 1N))

;; Neither recursive call is a tail call: addition still needs both results.
;; Keep n small; the amount of work grows exponentially.
(defn naive-fib [n]
  (if (< n 2)
    n
    (+' (naive-fib (- n 1))
        (naive-fib (- n 2)))))
