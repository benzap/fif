(ns fif.stdlib.ops-test
  (:refer-clojure :exclude [eval])
  (:require
   [clojure.test :refer :all]
   [fif.stdlib.ops :refer :all]
   [fif.core :refer [reval]]))


(deftest test-op+
  (is (= '(2) (reval 1 1 + >r))))


(deftest test-op-
  (is (= '(0) (reval 1 1 - >r)))
  (is (= '(1) (reval 2 1 - >r)))
  (is (= '(-1) (reval 1 2 - >r))))


(deftest test-op-plus-1
  (is (= '(2) (reval 1 inc >r))))


(deftest test-op-minus-1
  (is (= '(0) (reval 1 dec >r))))


(deftest test-op*
  (is (= '(4) (reval 2 2 * >r))))


(deftest test-op-div
  (is (= '(2) (reval 2 1 / >r)))
  (is (= '(1/2) (reval 1 2 / >r))))


(deftest test-op-mod
  (is (= '(0) (reval 2 1 mod >r)))
  (is (= '(1) (reval 1 2 mod >r))))


(deftest test-negate
  (is (= '(-1) (reval 1 negate >r)))
  (is (= '(1) (reval -1 negate >r))))


(deftest test-abs
  (is (= '(1) (reval 1 abs >r)))
  (is (= '(1) (reval -1 abs >r))))


(deftest test-op-max
  (is (= '(5) (reval 2 5 max >r)))
  (is (= '(2) (reval 2 -5 max >r))))


(deftest test-op-min
  (is (= '(2) (reval 2 5 min >r)))
  (is (= '(-5) (reval 2 -5 min >r))))


(deftest test-dup
  (is (= '(2 2) (reval 2 dup >r >r))))


(deftest test-dot)
(deftest test-carriage-return)
(deftest test-dot-stack)


(deftest test-push-return
  (is (= '(3 2) (reval 3 2 >r >r))))


(deftest test-pop-return
  (is (= '(2 3) (reval 1 2 3 >r >r >r r> r> r> >r >r))))


(deftest test-swap
  (is (= '(2 1) (reval 1 2 swap >r >r))))


(deftest test-rot
  (is (= '(1 2 3) (reval 3 1 2 rot >r >r >r))))


(deftest test-drop
  (is (= '(1) (reval 1 2 drop >r))))


(deftest test-nip
  (is (= '(1) (reval 1 2 swap nip >r))))


(deftest test-tuck
  (is (= '(2 1 2) (reval 1 2 tuck >r >r >r))))


(deftest test-over
  (is (= '(1 2 1) (reval 1 2 over >r >r >r))))


(deftest test-roll)
