(require '[sci.core :as sci])

;; Minimal reproduction of the JVM type boundary in sci.impl.types/Reified.
;; This is the Java interface from SCI's sci.impl.types dependency.
(try
  (sci/eval-string
    "(deftype Reified [interfaces meths protocols]
       sci.impl.types.ICustomType
       (getInterfaces [_] interfaces)
       (getMethods [_] meths)
       (getProtocols [_] protocols)
       (getFields [_] nil))"
    {:classes {'sci.impl.types.ICustomType sci.impl.types.ICustomType}})
  (println "PASS: SCI can interpret this interface implementation")
  (catch Throwable e
    (println "BLOCKED:" (ex-message e))
    (System/exit 1)))
