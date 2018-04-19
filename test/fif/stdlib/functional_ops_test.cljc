(ns fif.stdlib.functional-ops-test
  (:require
   [clojure.test :refer [deftest testing is are]]
   [fif.stdlib.functional-ops]
   [fif-test.utils :refer [teval are-eq*]]))


(deftest test-functional-mode-reduce
  (are-eq*
    (teval *+ [1 2 3 4] reduce)

    => '(10)))
