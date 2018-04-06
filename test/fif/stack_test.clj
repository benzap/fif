(ns fif.stack-test
  (:require [clojure.test :refer :all]
            [fif.stack :refer :all]))


(deftest test-stackmachine
  (testing "pushing and popping on the arg stack"
    (let [s (-> (new-stack-machine)
                (push-stack 1)
                (push-stack 2)
                (push-stack 3))]
      (is (= '(3 2 1) (get-stack s)))
      (is (= '(2 1) (-> s pop-stack get-stack)))
      (is (= '(3) (-> s pop-stack get-ret))))))



