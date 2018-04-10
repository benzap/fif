(ns fif.stdlib.compile-test
  (:refer-clojure :exclude [eval])
  (:require
   [clojure.test :refer :all]
   [fif.stdlib.compile :refer :all]
   [fif.core :refer [reval]]))


(deftest test-simple
  (is (= '(2 6) (reval
                 fn add2 2 + endfn
                 fn add4 add2 add2 endfn
                 0 add2 dup add4))))


(deftest test-recursive
  (is (= '(120) (reval
                 fn factorial
                 dup 1 > if dup dec factorial * then
                 endfn
                 
                 5 factorial))))


(deftest test-redefining
  (is (= '(1 2) (reval
                 fn add_num 1 + endfn
                 0 add_num
                 fn add_num 2 + endfn
                 0 add_num))))


(deftest test-inner-fn)
