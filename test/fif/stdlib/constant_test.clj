(ns fif.stdlib.constant-test
  (:refer-clojure :exclude [eval])
  (:require
   [clojure.test :refer :all]
   [fif.stdlib.constant :refer :all]
   [fif.core :refer [reval]]))


(deftest test-constant-init
  (is (= '(101) (reval 100 constant WATER-BOILING-POINT
                       true constant T
                       false constant F
                       WATER-BOILING-POINT 1 +))))
