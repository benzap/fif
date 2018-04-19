(ns fif.stack-machine.verification-test
  (:require
   [clojure.test :refer [deftest testing is are]]
   [fif.stack-machine.verification :as stack.verification]
   [fif.stack-machine :as stack]
   [fif.impl.stack-machine :refer [new-stack-machine]]))


(deftest test-stack-satisfies-arity?
  (let [sm (-> (new-stack-machine)
               (stack/push-stack 1)
               (stack/push-stack 2))]
    (is (not (stack.verification/stack-satisfies-arity? sm 3)))
    (is (stack.verification/stack-satisfies-arity? sm 2))
    (is (stack.verification/stack-satisfies-arity? sm 1))
    (is (stack.verification/stack-satisfies-arity? sm 0))))
