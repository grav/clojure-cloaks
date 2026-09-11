(require '[clojure.java.io :as io]
         '[clojure.string :as str]
         '[clojure.walk :as walk]
         '[sci.core :as sci]
         '[sci.impl.analyzer :as host-analyzer]
         '[sci.impl.utils :as host-utils]
         '[sci.impl.interpreter :as host-interpreter])
(import '[java.lang.reflect Proxy InvocationHandler])

(defn interface-object
  "Java interface shell; every supplied method body is an interpreted function."
  ([spec] (interface-object spec nil))
  ([{:keys [interfaces methods protocols fields] :as spec} metadata]
   (let [interfaces (into #{sci.impl.types.ICustomType clojure.lang.IObj}
                          (remove #{Object} (concat interfaces (keep :on-interface protocols))))]
     (Proxy/newProxyInstance
       (clojure.lang.RT/baseLoader)
       (into-array Class interfaces)
       (reify InvocationHandler
         (invoke [_ this method args]
           (let [n (symbol (.getName method))]
             (if-let [f (get methods n)]
               (apply f this (seq args))
               (case n
                 getInterfaces interfaces
                 getMethods methods
                 getProtocols protocols
                 getFields fields
                 meta metadata
                 withMeta (interface-object spec (first args))
                 toString "<interpreted SCI node>"
                 hashCode (System/identityHashCode this)
                 equals (identical? this (first args))
                 (throw (ex-info "Missing interface method" {:method n})))))))))))

;; These five namespaces are a declared host runtime, not self-hosted source.
(def foundation '#{sci.impl.types sci.impl.vars sci.lang sci.impl.records sci.impl.deftype})

(defn renamed-symbol [x]
  (if (and (symbol? x) (str/starts-with? (str x) "sci."))
    (with-meta (symbol (str "self." x)) (meta x))
    x))

(defn rename-form [form]
  (if (and (seq? form) (= 'quote (first form))
           (symbol? (second form)) (nil? (namespace (second form))))
    form
    (walk/walk rename-form renamed-symbol form)))

(defn copy-host-var [v target]
  (let [cv (sci/copy-var* v target)]
    (if (:macro (meta v))
      (sci/new-var (:name (meta v))
                   (fn [& args] (rename-form (apply @v args)))
                   (meta cv))
      cv)))

(def external-namespaces
  (into {}
        (for [n (all-ns)
              :let [sym (ns-name n)]
              :when (or (foundation sym)
                        (some #(str/starts-with? (str sym) %)
                              ["edamame." "clojure.tools.reader" "borkdude." "clojure.java.io" "clojure.core.protocols"]))
              :let [target (sci/create-ns (renamed-symbol sym))]]
          [(renamed-symbol sym) (into {} (for [[k v] (ns-publics n)]
                          [k (copy-host-var v target)]))])))

(def classes
  (into {'clojure.lang.LockingTransaction clojure.lang.LockingTransaction
         'ThreadLocal java.lang.ThreadLocal
         'java.lang.ThreadLocal java.lang.ThreadLocal
         'java.lang.IllegalStateException java.lang.IllegalStateException
         'sci.impl.types.ICustomType sci.impl.types.ICustomType}
        (for [n (all-ns) [_ c] (ns-imports n)]
          [(symbol (.getName c)) c])))

(def source-classes
  (into {}
        (for [f (file-seq (io/file (io/resource "sci")))
              :when (and (.isFile f) (str/ends-with? (.getName f) ".cljc"))
              token (re-seq #"(?:java|clojure|sci)\.[A-Za-z0-9_.$]+" (slurp f))
              :let [token (str/replace token #"\.$" "")
                    c (try (Class/forName token) (catch Throwable _ nil))]
              :when c]
          [(symbol token) c])))

(def loaded (atom #{}))

(defn load-source [{:keys [namespace]}]
  (assert (str/starts-with? (str namespace) "self.sci.")
          (str "Unexpected source dependency: " namespace))
  (let [namespace (symbol (subs (str namespace) 5))
        base (-> (str namespace) (str/replace "." "/") (str/replace "-" "_"))]
    (when-let [url (some io/resource (map #(str base %) [".clj" ".cljc"]))]
      (swap! loaded conj namespace)
      (println "Loading" namespace)
      (binding [*print-meta* true
                *ns* (or (find-ns namespace) (create-ns namespace))]
        (with-open [reader (java.io.PushbackReader. (io/reader url))]
          {:file (str url)
           :source
           (str/join "\n"
             (loop [forms []]
               (let [form (read {:read-cond :allow :features #{:clj} :eof ::end} reader)]
                 (cond
                   (= ::end form) forms
                   ;; Host Eval already treats these constants as themselves.
                   ;; Re-extending its protocol inside SCI is unsupported.
                   (and (= namespace 'sci.impl.evaluator)
                        (seq? form) (= 'extend-protocol (first form))
                        (= 'types/Eval (second form)))
                   (recur forms)
                   :else (recur (conj forms (pr-str (rename-form form))))))))})))))

(def core-extras
  [#'future-call #'slurp #'spit #'clojure.core/system-newline
   #'dosync #'sync #'ref #'alter #'commute #'ensure #'ref-set])

(def ctx
  (sci/init
    {:unrestricted true
     :load-fn load-source
     :features #{:clj}
     :namespaces
     (assoc external-namespaces 'clojure.core
            (into {} (for [v core-extras]
                       [(:name (meta v))
                        (sci/copy-var* v (sci/create-ns 'clojure.core))])))
     :classes
     (merge classes source-classes
            (into {} (map (fn [[k v]] [(renamed-symbol k) v])
                          (merge classes source-classes)))
            (into {} (for [[sym c] classes
                           :when (str/starts-with? (str sym) "java.lang.")]
                       [(symbol (.getSimpleName c)) c])))
     :reify-fn interface-object}))

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
