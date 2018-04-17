(ns fif.stack.processor-test
  (:require
   [clojure.test :refer :all]
   [fif.stack :as stack]
   [fif.stack.processor :refer :all]
   [fif-test.utils :refer [are-eq* teval]]))


(deftest test-pointer-evaluation
  (are-eq*
    (teval *x *y *z) => '(x y z)

    (teval x *y **z) => '(x y *z)

    (teval 1 2 * **) => '(2 *)))

