(ns fif.stdlib.ops-test
  (:require
   [clojure.test :refer [deftest testing is are]]
   [fif.stdlib.ops]
   [fif.stack-machine :as stack]
   [fif.core :as fif]
   [fif-test.utils :refer [teval are-eq*]]))


(deftest test-op+
  (is (= '(2) (teval 1 1 +))))


(deftest test-op-
  (is (= '(0) (teval 1 1 -)))
  (is (= '(1) (teval 2 1 -)))
  (is (= '(-1) (teval 1 2 -))))


(deftest test-op-plus-1
  (is (= '(2) (teval 1 inc))))


(deftest test-op-minus-1
  (is (= '(0) (teval 1 dec))))


(deftest test-op*
  (is (= '(4) (teval 2 2 *))))


(deftest test-op-div
  (is (= '(2) (teval 2 1 /)))
  (is (= '(1/2) (teval 1 2 /))))


(deftest test-op-mod
  (is (= '(0) (teval 2 1 mod)))
  (is (= '(1) (teval 1 2 mod))))


(deftest test-negate
  (is (= '(-1) (teval 1 negate)))
  (is (= '(1) (teval -1 negate))))


(deftest test-abs
  (is (= '(1) (teval 1 abs)))
  (is (= '(1) (teval -1 abs))))


(deftest test-op-max
  (is (= '(5) (teval 2 5 max)))
  (is (= '(2) (teval 2 -5 max))))


(deftest test-op-min
  (is (= '(2) (teval 2 5 min)))
  (is (= '(-5) (teval 2 -5 min))))


(deftest test-dup
  (is (= '(2 2) (teval 2 dup))))


(deftest test-dot)
(deftest test-carriage-return)
(deftest test-dot-stack)


(deftest test-push-return
  (is (= '(3 2) (-> (fif/eval 3 2 >r >r) stack/get-ret))))


(deftest test-pop-return
  (is (= '(2 3) (-> (fif/eval 1 2 3 >r >r >r r> r> r> >r >r) stack/get-ret))))


(deftest test-swap
  (is (= '(2 1) (teval 1 2 swap))))


(deftest test-rot
  (is (= '(1 2 3) (teval 3 1 2 rot))))


(deftest test-drop
  (is (= '(1) (teval 1 2 drop))))


(deftest test-nip
  (is (= '(1) (teval 1 2 swap nip))))


(deftest test-tuck
  (is (= '(2 1 2) (teval 1 2 tuck))))


(deftest test-over
  (is (= '(1 2 1) (teval 1 2 over))))


(deftest test-roll)
