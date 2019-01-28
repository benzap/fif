(ns fif.stdlib.piecewise-test
  (:require
   [clojure.test :refer [deftest testing is are] :include-macros true]
   [fif.stdlib.piecewise]
   [fif.stack-machine :as stack]
   [fif.core :as fif]
   [fif-test.utils :refer [teval are-eq*] :include-macros true]))


(deftest test-%-op
  (are-eq*
    (teval 2 2 + %)
  
    => '(4)

    (teval 2 2 % +)
  
    => '(4)

    (teval
     def x 10     

     2 *x % +)
  
    => '(12)))


(deftest test-%1-op
  (are-eq*
    (teval 2 2 + %1)
  
    => '(4)

    (teval 2 2 %1 +)
  
    => '(4)

    (teval
     def x 10     

     2 *x %1 +)
  
    => '(12)))


(deftest test-%2-op
  (are-eq*

    (teval 2 2 %2 +)
  
    => '(4)

    (teval
     def x 10     

     *x 2 %2 +)
  
    => '(12)))


(deftest test-%3-op
  (are-eq*

    (teval 2 2 2 %3 +)
  
    => '(2 4)

    (teval
     def x 10     

     *x 2 1 %3 + *)
  
    => '(22)))
