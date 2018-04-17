(ns fif.stdlib.variable-test
  (:require
   [clojure.test :refer :all]
   [fif.stdlib.variable :refer :all]
   [fif-test.utils :refer [teval are-eq*]]))


(deftest test-variable-creation
  (is (= '(20) (teval def x 20 x getv))))


(deftest test-variable-set
  (is (= '(2) (teval def x 1
                     def y 2
                     x dup dup getv inc swap setv getv))))
