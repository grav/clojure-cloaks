(require '[clojure.java.io :as io]
         '[clojure.string :as str]
         '[sci.core :as sci])

;; Resolve source, never host SCI functions, into the inner context.
(defn load-source [{:keys [namespace]}]
  (let [base (-> (str namespace) (str/replace "." "/") (str/replace "-" "_"))]
    (when-let [url (some io/resource (map #(str base %) [".clj" ".cljc"]))]
      (println "Loading" namespace)
      {:file (str url) :source (slurp url)})))

;; Optional boundary: host reader/locking libraries; all sci.* namespaces
;; must still be loaded from source.
(def host-deps? (= "--host-deps" (first *command-line-args*)))
(def external-namespaces
  (when host-deps?
    (into {}
          (for [n (all-ns)
                :let [sym (ns-name n)]
                :when (some #(str/starts-with? (str sym) %)
                            ["edamame." "clojure.tools.reader" "borkdude."])
                :let [target (sci/create-ns sym)]]
            [sym (into {} (for [[k v] (ns-publics n)]
                            [k (sci/copy-var* v target)]))]))))

(try
  (let [result (sci/eval-string
                "(require '[sci.core :as inner]) (inner/eval-string \"(+ 1 2)\")"
                {:load-fn load-source :features #{:clj}
                 :namespaces external-namespaces
                 :classes (when host-deps?
                            {'java.lang.IllegalStateException java.lang.IllegalStateException
                             'sci.impl.types.ICustomType sci.impl.types.ICustomType})})]
    (assert (= 3 result))
    (println "PASS: interpreted SCI evaluated (+ 1 2):" result))
  (catch Throwable e
    (println "BLOCKED:" (ex-message e))
    (prn (select-keys (ex-data e) [:type :phase :file :line :column]))
    (System/exit 1)))
