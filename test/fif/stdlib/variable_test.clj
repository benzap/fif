(ns fif.stdlib.variable-test
  (:refer-clojure :exclude [eval])
  (:require
   [clojure.test :refer :all]
   [fif.stdlib.variable :refer :all]
   [fif.core :refer [reval]]))


(deftest test-variable-creation
  (is (= '(20) (reval def x 20 x getv))))


(deftest test-variable-set
  (is (= '(2) (reval def x 1
                     def y 2
                     x dup dup getv inc swap setv getv))))
