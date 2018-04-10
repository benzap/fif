(ns fif.stdlib.conditional-test
  (:refer-clojure :exclude [eval])
  (:require
   [clojure.test :refer :all]
   [fif.stdlib.conditional :refer :all]
   [fif.core :refer [reval]]))


(deftest test-conditional-if-then
  (is (= '(2 1) (reval 2 0 > if 2 then 1)))
  (is (= '(1) (reval 2 0 < if 2 then 1))))


(deftest test-conditional-if-else-then
  (is (= '(1 3) (reval 2 0 > if 1 else 2 then 3)))
  (is (= '(2 3) (reval 2 0 < if 1 else 2 then 3))))


(deftest test-conditional-nested
  (is (= '(1 3) (reval 2 0 > if
                                true 
                                if 1 then 
                             else 
                                false
                                if 2 then 
                             then 3))))


(deftest test-conditional-advanced
  (is (= '("You are the right age") 
         (reval
          19 
          dup 18 <  if drop "You are underage"      else
          dup 50 <  if drop "You are the right age" else
          dup 50 >= if drop "You are too old"       else
          then then then))))
