(require '[self-host :as boot]
         '[sci.core :as sci]
         '[sci.impl.analyzer :as host-analyzer]
         '[sci.impl.utils :as host-utils]
         '[sci.impl.interpreter :as host-interpreter])

(def implementation
  "(require '[self.sci.core :as inner])
   {:eval* inner/eval-string*
    :init inner/init
    :ctx-var (var self.sci.ctx-store/*ctx*)
    :analyze self.sci.impl.analyzer/analyze
    :resolve self.sci.impl.evaluator/eval-resolve
    :out inner/out
    :err inner/err}")

(defn forbidden [& _]
  (throw (ex-info "Unexpected host analyzer/eval entry" {})))

(defn call-engine [engine f & args]
  (if (zero? (:depth engine))
    (sci/binding [sci/out *out* sci/err *err*] (apply f args))
    (binding [sci.ctx-store/*ctx* (:host-ctx engine)]
      (with-redefs [host-utils/analyze (volatile! (:analyze engine))
                    host-utils/eval-resolve-state (volatile! (:resolve engine))
                    sci/eval-string forbidden
                    sci/eval-string* forbidden
                    host-interpreter/eval-string* forbidden
                    host-analyzer/analyze forbidden]
        (sci/with-bindings (assoc (:bindings engine)
                                 (:out engine) *out* (:err engine) *err*
                                 (:ctx-var engine) (if (:env (first args)) (first args) {:unrestricted true}))
          (apply f args))))))

(defn load-child [parent]
  (let [context (call-engine parent (:init parent) boot/options)
        api (call-engine parent (:eval* parent) context implementation)]
    (assoc api
           :depth (inc (:depth parent))
           :host-ctx (if (zero? (:depth parent)) context (:host-ctx parent))
           :bindings (if (zero? (:depth parent))
                       {}
                       (assoc (:bindings parent) (:ctx-var parent) context
                              (:out parent) *out* (:err parent) *err*)))))

(try
  (let [[flag n source & extra] *command-line-args*
        n (when n (parse-long n))]
    (when-not (and (= "-n" flag) n (<= 0 n) source (empty? extra))
      (throw (ex-info "Usage: clj -M tower.clj -n N 'expression'" {})))
    (loop [engine {:depth 0 :init sci/init :eval* sci/eval-string*}]
      (let [depth (:depth engine)]
        (if (= depth n)
          (let [context (call-engine engine (:init engine) {})
                calls (atom {})
                result (binding [boot/*layer* (inc n) boot/*node-calls* calls]
                         (call-engine engine (:eval* engine) context source))]
            (when (some? result) (prn result))
            (binding [*out* *err*]
              (println "Interpreted node calls by layer:" (into (sorted-map) @calls))))
          (let [next-depth (inc depth)
                start (System/nanoTime)
                loaded (atom #{})
                child (binding [boot/*layer* next-depth
                                boot/*verbose-loading* false
                                boot/loaded loaded]
                        (load-child engine))]
            (assert (every? @loaded
                            '[sci.core sci.impl.analyzer sci.impl.evaluator sci.impl.interpreter])
                    (str "Layer " next-depth " did not load the implementation source"))
            (binding [*out* *err*]
              (println "Layer" next-depth "ready in"
                       (/ (- (System/nanoTime) start) 1e9) "seconds"))
            (recur (assoc child :depth next-depth)))))))
  (catch Throwable e
    (binding [*out* *err*]
      (println "Tower failed:" (ex-message e))
      (doseq [cause (take 8 (take-while some? (iterate ex-cause e)))]
        (println (.getName (class cause)) (ex-message cause)))
      (prn (select-keys (ex-data e) [:type :phase :file :line :column])))
    (System/exit 1)))
