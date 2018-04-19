(ns fif.stack-machine.processor-test
  (:require
   [clojure.test :refer [deftest testing is are]]
   [fif.stack-machine :as stack]
   [fif.stack-machine.processor :as stack.processor]
   [fif-test.utils :refer [are-eq* teval]]))


(deftest test-pointer-evaluation
  (are-eq*
    (teval *x *y *z) => '(x y z)

    (teval x *y **z) => '(x y *z)

    (teval 1 2 * **) => '(2 *)))

