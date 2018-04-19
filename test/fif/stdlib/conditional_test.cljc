(ns fif.stdlib.conditional-test
  (:require
   [clojure.test :refer [deftest testing is are]]
   [fif-test.utils :refer [teval are-eq*]]))


(deftest test-conditional-if-then
  (testing "Main Tests"
    (are-eq*

      (teval 2 0 > if 2 then 1) => '(2 1)
  
      (teval 2 0 < if 2 then 1) => '(1))))


(deftest test-conditional-if-else-then
  (testing "Main Tests"
    (are-eq*
      
      (teval 2 0 > if 1 else 2 then 3) => '(1 3)

      (teval 2 0 < if 1 else 2 then 3) => '(2 3))))


(deftest test-conditional-nested
  (testing "Main Tests"
    (are-eq*
      (teval
       2 0 > if
         true if 1 then
       else
         false if 2 then
       then 3)

      => '(1 3))))


(deftest test-conditional-advanced
  (testing "Main Tests"
    (are-eq*
      (teval
       19 
       dup 18 <  if drop "You are underage"      else
       dup 50 <  if drop "You are the right age" else
       dup 50 >= if drop "You are too old"       else
       then then then)

      => '("You are the right age"))))
