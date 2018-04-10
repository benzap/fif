(ns fif.stdlib.ops-test
  (:refer-clojure :exclude [eval])
  (:require
   [clojure.test :refer :all]
   [fif.stdlib.ops :refer :all]
   [fif.core :refer [eval reval get-ret]]))


(deftest test-op+
  (is (= '(2) (reval 1 1 +))))


(deftest test-op-
  (is (= '(0) (reval 1 1 -)))
  (is (= '(1) (reval 2 1 -)))
  (is (= '(-1) (reval 1 2 -))))


(deftest test-op-plus-1
  (is (= '(2) (reval 1 inc))))


(deftest test-op-minus-1
  (is (= '(0) (reval 1 dec))))


(deftest test-op*
  (is (= '(4) (reval 2 2 *))))


(deftest test-op-div
  (is (= '(2) (reval 2 1 /)))
  (is (= '(1/2) (reval 1 2 /))))


(deftest test-op-mod
  (is (= '(0) (reval 2 1 mod)))
  (is (= '(1) (reval 1 2 mod))))


(deftest test-negate
  (is (= '(-1) (reval 1 negate)))
  (is (= '(1) (reval -1 negate))))


(deftest test-abs
  (is (= '(1) (reval 1 abs)))
  (is (= '(1) (reval -1 abs))))


(deftest test-op-max
  (is (= '(5) (reval 2 5 max)))
  (is (= '(2) (reval 2 -5 max))))


(deftest test-op-min
  (is (= '(2) (reval 2 5 min)))
  (is (= '(-5) (reval 2 -5 min))))


(deftest test-dup
  (is (= '(2 2) (reval 2 dup))))


(deftest test-dot)
(deftest test-carriage-return)
(deftest test-dot-stack)


(deftest test-push-return
  (is (= '(3 2) (-> (eval 3 2 >r >r) get-ret))))


(deftest test-pop-return
  (is (= '(2 3) (-> (eval 1 2 3 >r >r >r r> r> r> >r >r) get-ret))))


(deftest test-swap
  (is (= '(2 1) (reval 1 2 swap))))


(deftest test-rot
  (is (= '(1 2 3) (reval 3 1 2 rot))))


(deftest test-drop
  (is (= '(1) (reval 1 2 drop))))


(deftest test-nip
  (is (= '(1) (reval 1 2 swap nip))))


(deftest test-tuck
  (is (= '(2 1 2) (reval 1 2 tuck))))


(deftest test-over
  (is (= '(1 2 1) (reval 1 2 over))))


(deftest test-roll)
