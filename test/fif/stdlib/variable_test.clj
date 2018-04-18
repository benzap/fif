(ns fif.stdlib.variable-test
  (:require
   [clojure.test :refer :all]
   [fif.stdlib.variable :refer :all]
   [fif-test.utils :refer [teval are-eq*]]))


(deftest test-variable-creation
  (are-eq*

   (teval def x 20 x)
   
   => '(20)))


(deftest test-variable-set
  (are-eq*
   (teval
    def x 1
    def y 2

    *x 10 setg x)
  
   => '(10)))


(deftest test-variable-let
  (are-eq*
   (teval
    let x 1
    let y 2

    *x 10 setl x)
    
  
   => '(10)))


(deftest test-variable-let2
  (are-eq*
   (teval
    let x 1
    
    fn push2
    let x 2
    x
    endfn

    x push2)
    
  
   => '(1 2)))
