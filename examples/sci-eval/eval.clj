(require '[sci.core :as sci])

(sci/eval-string (first *command-line-args*)
                 {:bindings {'println println 'prn prn}})
