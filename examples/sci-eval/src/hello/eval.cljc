(ns hello.eval
  (:require [sci.core :as sci]))

(defn evaluate [expression]
  (sci/eval-string expression {:bindings {'println println 'prn prn}}))

#?(:clj (evaluate (first *command-line-args*))
   :cljs (do
           (enable-console-print!)
           (set! *main-cli-fn* evaluate))
   :cljd (defn main [args]
           (evaluate (first args))))
