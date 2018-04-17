(ns fif.stack-machine.processor-test
  (:require
   [clojure.test :refer :all]
   [fif.stack-machine :as stack]
   [fif.stack-machine.processor :refer :all]
   [fif-test.utils :refer [are-eq* teval]]))


(deftest test-pointer-evaluation
  (are-eq*
    (teval *x *y *z) => '(x y z)

    (teval x *y **z) => '(x y *z)

    (teval 1 2 * **) => '(2 *)))

