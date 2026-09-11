(require '[self-host :refer [ctx loaded]]
         '[sci.core :as sci]
         '[sci.impl.analyzer :as host-analyzer]
         '[sci.impl.utils :as host-utils]
         '[sci.impl.interpreter :as host-interpreter])

(try
  (sci/eval-string* ctx "(require '[self.sci.core :as inner])")
  (assert (every? @loaded '[sci.core sci.impl.analyzer sci.impl.interpreter sci.impl.evaluator]))
  (let [inner-eval (sci/eval-string* ctx "inner/eval-string")
        inner-analyze (sci/eval-string* ctx "self.sci.impl.analyzer/analyze")
        inner-resolve (sci/eval-string* ctx "self.sci.impl.evaluator/eval-resolve")
        cases [["greeting" "(str \"Hello from interpreted SCI!\")" "Hello from interpreted SCI!"]
               ["arithmetic" "(+ 1 2)" 3]
               ["closure" "((let [x 40] (fn [y] (+ x y))) 2)" 42]
               ["recursion" "(defn fact [n] (if (zero? n) 1 (* n (fact (dec n))))) (fact 8)" 40320]
               ["loop/recur" "(loop [n 1000 sum 0] (if (zero? n) sum (recur (dec n) (+ sum n))))" 500500]
               ["macro" "(defmacro twice [x] (list '+ x x)) (twice 21)" 42]
               ["atom" "(let [a (atom 1)] (swap! a + 41) @a)" 42]
               ["exception" "(try (throw (ex-info \"test\" {:x 42})) (catch Exception e (:x (ex-data e))))" 42]
               ["protocol and record" "(defprotocol P (value [x])) (defrecord R [n] P (value [_] n)) (value (->R 42))" 42]
               ["metadata" "(meta (with-meta [1] {:answer 42}))" {:answer 42}]
               ["destructuring" "(let [{:keys [x]} {:x 40} [y] [2]] (+ x y))" 42]
               ["multimethod" "(defmulti f :kind) (defmethod f :answer [x] (:n x)) (f {:kind :answer :n 42})" 42]
               ["reify" "(defprotocol P (value [x])) (value (reify P (value [_] 42)))" 42]
               ["type" "(deftype T [x] Object (toString [_] (str x))) (str (T. 42))" "42"]
               ["dynamic binding" "(def ^:dynamic *x* 1) [(binding [*x* 42] *x*) *x*]" [42 1]]]
        forbidden (fn [& _] (throw (ex-info "Unexpected host analyzer/eval entry" {})))]
    ;; The host type compiler's callbacks must return to the interpreted
    ;; analyzer. Root rebinding is scoped and restored, but is process-wide:
    ;; this experimental runner is deliberately single-threaded.
    (with-redefs [host-utils/analyze (volatile! inner-analyze)
                  host-utils/eval-resolve-state (volatile! inner-resolve)
                  sci/eval-string forbidden
                  sci/eval-string* forbidden
                  host-interpreter/eval-string* forbidden
                  host-analyzer/analyze forbidden]
      (doseq [[label source expected] cases]
        (let [actual (binding [sci.ctx-store/*ctx* ctx] (inner-eval source))]
          (assert (= expected actual) (str label ": " actual))
          (println "PASS:" label actual)))
      (binding [sci.ctx-store/*ctx* ctx]
        (inner-eval "(def only-in-this-context 42)")
        (assert (nil? (inner-eval "(resolve 'only-in-this-context)")))
        (println "PASS: fresh contexts are isolated")
        (when-let [source (first *command-line-args*)]
          (prn (inner-eval source))))
      (println "PASS: host analyzer/eval entry points stayed disabled")))
  (catch Throwable e
    (println "BLOCKED:" (ex-message e))
    (prn (select-keys (ex-data e) [:type :phase :file :line :column]))
    (System/exit 1)))
