(ns fif.stdlib.compile-test
  (:require
   [clojure.test :refer [deftest testing is are]]
   [fif.stdlib.compile]
   [fif-test.utils :refer [teval are-eq*]]))


(deftest test-simple
  (testing "Main Tests"
    (are-eq*
      (teval
       fn add2 2 + endfn
       fn add4 add2 add2 endfn

       0 add2 dup add4)
       
      => '(2 6))))


(deftest test-recursive
  (testing "Main Tests"
    (are-eq*
      (teval
       fn factorial
       dup 1 > if dup dec factorial * then
       endfn

       5 factorial
       3 factorial)

      => '(120 6))))


(deftest test-redefining
  (testing "Main Tests"
    (are-eq*
      (teval
       fn add_num 1 + endfn
       0 add_num
       fn add_num 2 + endfn
       0 add_num)
    
     => '(1 2))))


(deftest test-inner-fn
  (testing "Main Tests"
    (are-eq*
      (teval
       fn define_as_inc fn func 1 + endfn endfn
       fn define_as_dec fn func 1 - endfn endfn
       
       define_as_inc
       1 func
       
       define_as_dec
       1 func)

     => '(2 0))))
