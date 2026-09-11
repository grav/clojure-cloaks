(ns tower-test
  (:require [tower :as tower]
            [clojure.test :refer [deftest is testing run-tests]]))

(deftest hello-through-four-copies
  (doseq [depth (range 5)]
    (testing (str depth " interpreter copies")
      (is (= "Hello, world!" (tower/run depth tower/hello))))))

(deftest semantics-survive-self-interpretation
  (doseq [depth (range 4)]
    (testing (str "semantics at depth " depth)
      (is (= 120 (tower/run depth
                   '((fn factorial [n]
                       (if (<= n 1) 1 (* n (factorial (- n 1))))) 5))))
      (is (= 12 (tower/run depth
                  '(let [x 10
                         add (fn [y] (+ x y))]
                     (let [x 99] (add 2))))))
      (is (= [3 '(+ 1 2) false nil]
             (tower/run depth '(let [x 1 y (+ x 2)]
                                 [y (quote (+ 1 2)) false nil]))))
      (is (= 42 (tower/run depth '(if false (throw (ex-info "Unchosen branch" {})) 42))))
      (is (= 42 (tower/run depth '(if 0 42 (throw (ex-info "Unchosen branch" {})))))))))

(deftest errors-survive-self-interpretation
  (doseq [depth (range 1 4)]
    (is (thrown-with-msg? Exception #"Unbound symbol"
          (tower/run depth 'missing-symbol)))
    (is (thrown-with-msg? Exception #"Wrong argument count"
          (tower/run depth '((fn [x] x) 1 2)))))
  (is (thrown? Exception (tower/run -1 tower/hello))))

(let [{:keys [fail error]} (run-tests 'tower-test)]
  (when (pos? (+ fail error)) (System/exit 1)))
