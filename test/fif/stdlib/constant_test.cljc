(ns fif.stdlib.constant-test
  (:require
   [clojure.test :refer [deftest testing is are]]
   [fif-test.utils :refer [teval are-eq*]]))


(deftest test-constant-init
  (is (= '(101) (teval 100 constant WATER-BOILING-POINT
                       true constant T
                       false constant F
                       WATER-BOILING-POINT 1 +))))
