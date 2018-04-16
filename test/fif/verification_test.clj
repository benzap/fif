(ns fif.verification-test
  (:require
   [clojure.test :refer :all]
   [fif.verification :refer :all]
   [fif.stack :as stack]))


(deftest test-stack-satisfies-arity?
  (let [sm (-> (stack/new-stack-machine)
               (stack/push-stack 1)
               (stack/push-stack 2))]
    (is (not (stack-satisfies-arity? sm 3)))
    (is (stack-satisfies-arity? sm 2))
    (is (stack-satisfies-arity? sm 1))
    (is (stack-satisfies-arity? sm 0))))
