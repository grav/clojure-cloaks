(ns hello.eval
  (:require [sci.core :as sci]))

(enable-console-print!)

(defn -main [expression]
  (sci/eval-string expression {:bindings {'println println 'prn prn}}))

(set! *main-cli-fn* -main)
