(ns fif.stdlib.ops-test
  (:require
   [clojure.test :refer [deftest testing is are] :include-macros true]
   [fif.stdlib.ops]
   [fif.stack-machine :as stack]
   [fif.core :as fif]
   [fif-test.utils :refer [teval are-eq*] :include-macros true]))


;;
;; Arithmetic
;;


(deftest test-*-op
  (are-eq*
   (teval 2 2 *)

   => '(4)

   (teval 2 2 * 2 *)

   => '(8)))


(deftest test-+-op
  (are-eq*
   (teval 2 2 +)

   => '(4)

   (teval 2 2 + 2 +)

   => '(6)))


(deftest test---op
  (are-eq*
   (teval 2 2 -)

   => '(0)

   (teval 4 2 -)

   => '(2)

   (teval 2 4 -)

   => '(-2)))


(deftest test-divide-op
  (are-eq*
   (teval 2 2 /)

   => '(1)


   (teval 4 2 /)

   => '(2)))

;; FIXME: ratios are not recognized in js
;;#?(:clj (is (= '(1/2) (teval 1 2 /)))))


(deftest test-abs-op
  (are-eq*
   (teval 2 abs)

   => '(2)

   (teval -2 abs)

   => '(2)))


(deftest test-dec-op
  (are-eq*
   (teval 2 dec)

   => '(1)

   (teval -3 dec)

   => '(-4)))


(deftest test-inc-op
  (are-eq*
   (teval 2 inc)

   => '(3)

   (teval -3 inc)

   => '(-2)))



(deftest test-max-op
  (are-eq*
   (teval 2 4 max)

   => '(4)

   (teval 2 -4 max)

   => '(2)))


(deftest test-mod-op
  (are-eq*
   (teval 2 1 mod)

   => '(0)

   (teval 1 2 mod)

   => '(1)))


(deftest test-negate-op
  (are-eq*
   (teval 1 negate)

   => '(-1)

   (teval -1 negate)

   => '(1)))



(deftest test-negate-op
  (are-eq*
   (teval 1 dup dup)

   => '(1 1 1)))


(deftest test-quot-op
  (are-eq*
   (teval 1 2 quot)

   => '(0)))


(deftest test-rem-op
  (are-eq*
   (teval 1 2 rem)

   => '(1)))

;;
;; Bitwise Tests
;;





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
