(require '[planck.core :refer [eval read-string]])

(eval (read-string (first *command-line-args*)))
