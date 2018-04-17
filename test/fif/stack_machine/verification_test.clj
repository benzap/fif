(ns fif.stack-machine.verification-test
  (:require
   [clojure.test :refer :all]
   [fif.stack-machine.verification :refer :all]
   [fif.stack-machine :as stack]
   [fif.impl.stack-machine :refer [new-stack-machine]]))


(deftest test-stack-satisfies-arity?
  (let [sm (-> (new-stack-machine)
               (stack/push-stack 1)
               (stack/push-stack 2))]
    (is (not (stack-satisfies-arity? sm 3)))
    (is (stack-satisfies-arity? sm 2))
    (is (stack-satisfies-arity? sm 1))
    (is (stack-satisfies-arity? sm 0))))
