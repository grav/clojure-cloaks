;; This entire expression belongs to the subset it implements.
;; No eval, SCI API, host compiler, macros, mutation or namespace loading.
(fn evaluate [form env]
  (let [bind-args
        (fn bind-args [params args scope]
          (if (empty? params)
            scope
            (bind-args (rest params) (rest args)
                       (assoc scope (first params) (first args)))))
        eval-items
        (fn eval-items [items scope]
          (if (empty? items)
            (quote ())
            (cons (evaluate (first items) scope)
                  (eval-items (rest items) scope))))
        eval-bindings
        (fn eval-bindings [pairs scope]
          (if (empty? pairs)
            scope
            (eval-bindings (rest (rest pairs))
                           (assoc scope (first pairs)
                                  (evaluate (nth pairs 1) scope)))))
        invoke
        (fn invoke [f args]
          (if (fn? f)
            (apply f args)
            (if (= (count (nth f 2)) (count args))
              (evaluate (nth f 3)
                        (bind-args (nth f 2) args
                                   (if (nth f 1)
                                     (assoc (nth f 4) (nth f 1) f)
                                     (nth f 4))))
              (throw (ex-info "Wrong argument count" {})))))]
    (if (symbol? form)
      (if (contains? env form)
        (get env form)
        (throw (ex-info "Unbound symbol" (hash-map :symbol form))))
      (if (seq? form)
        (if (empty? form)
          form
          (let [op (first form)]
            (if (= op (quote quote))
              (nth form 1)
              (if (= op (quote if))
                (if (evaluate (nth form 1) env)
                  (evaluate (nth form 2) env)
                  (evaluate (nth form 3) env))
                (if (= op (quote let))
                  (evaluate (nth form 2) (eval-bindings (nth form 1) env))
                  (if (= op (quote fn))
                    (if (symbol? (nth form 1))
                      [:closure (nth form 1) (nth form 2) (nth form 3) env]
                      [:closure nil (nth form 1) (nth form 2) env])
                    (if (= op (quote throw))
                      (throw (evaluate (nth form 1) env))
                      (invoke (evaluate op env) (eval-items (rest form) env)))))))))
        (if (vector? form)
          (vec (eval-items form env))
          form)))))
