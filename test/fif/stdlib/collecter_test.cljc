(ns fif.stdlib.collecter-test
  (:require 
   [clojure.test :refer :all]
   [fif.stdlib.collecter :refer :all]
   [fif-test.utils :refer [are-eq* teval]]))
            

(deftest test-collecter-into-list
  (testing "<-into! operator with lists"
    (are-eq*
     (teval () <-into! 1 2 3 !) => '((3 2 1))
     (teval (4 5) <-into! 3 1 do i loop !) => '((3 2 1 4 5))
     (teval 1 (4 5) <-into! 3 1 do i loop ! true) => '(1 (3 2 1 4 5) true)))

  (testing "list! operator"
    (are-eq*
     (teval list! 1 2 3 !) => '((3 2 1))
     (teval nil list! 1 2 3 ! true) => '(nil (3 2 1) true)))

  (testing "Nested List Values"
    (are-eq*
     (teval list! 1 2 3 list! 4 5 6 ! !) => '(((6 5 4) 3 2 1)))))


(deftest test-collecter-into-vector
  (testing "<-into! operator with vectors"
    (are-eq*
     (teval [] <-into! 1 2 3 !) => '([1 2 3])
     (teval [1 2] <-into! 3 4 5 !) => '([1 2 3 4 5])
     (teval [] <-into! 3 0 do i loop ! 4) => '([0 1 2 3] 4)))

  (testing "vec! operator"
    (are-eq*
     (teval vec! 1 2 3 !) => '([1 2 3])
     (teval 0 vec! [1 2 3] 4 5 ! 6) => '(0 [[1 2 3] 4 5] 6)))

  (testing "Nested Vector values"
    (are-eq*
     (teval vec! 2 0 do vec! 0 i ! loop !) => '([[0 0] [0 1] [0 2]]))))


(deftest test-collecter-into-map
  (testing "<-into! operator with maps"
    (are-eq*
      (teval {} <-into! [:a 123] [:b 345] !) => '({:a 123 :b 345})
      (teval {:a "prev value"} <-into! [:a 123] [:b 345] !) => '({:a 123 :b 345})
      (teval {} <-into! :a 123 pair :b 345 pair !) => '({:a 123 :b 345})))

  (testing "map! operator"
    (are-eq*
      (teval map! [:a 123] [:b 345] !) => '({:a 123 :b 345})
      (teval map! :a 123 pair :b 345 pair !) => '({:a 123 :b 345}))))
