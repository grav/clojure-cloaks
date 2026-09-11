(ns self-host
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [sci.core :as sci]
            [sci.impl.analyzer :as host-analyzer]
            [sci.impl.utils :as host-utils]
            [sci.impl.interpreter :as host-interpreter])
  (:import [java.lang.reflect Proxy InvocationHandler]))

(def ^:dynamic *layer* 1)
(def ^:dynamic *node-calls* nil)

(defn interface-object
  "Java interface shell; every supplied method body is an interpreted function."
  ([spec] (interface-object spec nil))
  ([{:keys [interfaces methods protocols fields] :as spec} metadata]
   (let [owner (or (::node-owner spec) (dec *layer*))
         spec (assoc spec ::node-owner owner)
         interfaces (into #{sci.impl.types.ICustomType clojure.lang.IObj}
                          (remove #{Object} (concat interfaces (keep :on-interface protocols))))]
     (Proxy/newProxyInstance
       (clojure.lang.RT/baseLoader)
       (into-array Class interfaces)
       (reify InvocationHandler
         (invoke [_ this method args]
           (let [n (symbol (.getName method))]
             (when (and *node-calls* (= 'eval n))
               (swap! *node-calls* update owner (fnil inc 0)))
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

;; A single host scope keeps an inner push/pop pair from being separated
;; by an outer interpreter's temporary dynamic-binding frames.
(defn scoped-bindings [bindings f & args]
  (sci/with-bindings bindings (apply f args)))

(defn binding-form [_form _env bindings body]
  (assert (and (vector? bindings) (even? (count bindings)))
          "binding requires a vector with an even number of forms")
  (let [pairs (mapcat (fn [[v value]] [(list 'var v) value]) (partition 2 bindings))]
    (list 'clojure.core/with-bindings*
          (cons 'hash-map pairs)
          (list* 'fn [] body))))

(defn adapt-form [namespace form]
  (if (and (= namespace 'sci.impl.namespaces) (seq? form) (= 'defn (first form)))
    (case (second form)
      with-bindings*
      '(defn with-bindings* [bindings f & args]
         (apply bridge/scoped-bindings bindings f args))
      sci-binding
      '(defn sci-binding [form env bindings & body]
         (bridge/binding-form form env bindings body))
      form)
    form))

(def ^:dynamic loaded (atom #{}))
(def ^:dynamic *verbose-loading* true)

(defn load-source [{:keys [namespace]}]
  (assert (str/starts-with? (str namespace) "self.sci.")
          (str "Unexpected source dependency: " namespace))
  (let [namespace (symbol (subs (str namespace) 5))
        base (-> (str namespace) (str/replace "." "/") (str/replace "-" "_"))]
    (when-let [url (some io/resource (map #(str base %) [".clj" ".cljc"]))]
      (swap! loaded conj namespace)
      (when *verbose-loading*
        (binding [*out* *err*] (println "Layer" *layer* "loading" namespace)))
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
                   :else (recur (conj forms (pr-str (rename-form (adapt-form namespace form)))))))))})))))

(def core-extras
  [#'print-method #'print-dup #'future-call #'slurp #'spit #'clojure.core/system-newline
   #'dosync #'sync #'ref #'alter #'commute #'ensure #'ref-set])

(def options
    {:unrestricted true
     :load-fn load-source
     :features #{:clj}
     :namespaces
     (assoc external-namespaces
            'bridge {'scoped-bindings scoped-bindings 'binding-form binding-form}
            'clojure.core
            (into {} (for [v core-extras]
                       [(:name (meta v))
                        (sci/copy-var* v (sci/create-ns 'clojure.core))])))
     :classes
     (merge {:allow :all} classes source-classes
            (into {} (map (fn [[k v]] [(renamed-symbol k) v])
                          (merge classes source-classes)))
            (into {} (for [[sym c] classes
                           :when (str/starts-with? (str sym) "java.lang.")]
                       [(symbol (.getSimpleName c)) c])))
     :reify-fn interface-object})

(def ctx (sci/init options))

