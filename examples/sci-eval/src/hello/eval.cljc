(ns hello.eval
  (:require [sci.core :as sci]))

#?(:clj
   (prn (sci/eval-string (first *command-line-args*)))
   :cljs
   (do
     (enable-console-print!)
     (prn (sci/eval-string (aget js/process.argv 2))))
   :cljd
   (defn main [args]
     (prn (sci/eval-string (first args)))))
