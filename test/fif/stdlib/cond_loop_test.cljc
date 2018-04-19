(ns fif.stdlib.cond-loop-test
  (:require
   [clojure.test :refer [deftest testing is are]]
   [fif.stdlib.cond-loop-test]
   [fif-test.utils :refer [teval are-eq*]]))


(deftest test-do-loop
  (testing "Main Tests"
    (are-eq*
      (teval 4 0 do i loop) => '(0 1 2 3 4)

      (teval 4 0 do leave 1 2 3 4 loop 5) => '(5)

      (teval 4 0 do i 2 = if leave then i loop) => '(0 1)

      (teval 0 0 do i loop) => '(0))))


(deftest test-do-plus-loop
  (testing "Main Tests"
    (are-eq*
      (teval 8 0 do i 2 +loop) => '(0 2 4 6 8))))
           

(deftest test-begin-until
  (testing "Main Tests"
    (are-eq*
      (teval 0 begin dup inc dup 5 = until) => '(0 1 2 3 4 5)
     
      (teval 0 begin dup inc inc leave 5 <= until) => '(0 2)

      (teval begin 1 begin 2 leave true until 3 leave false until) => '(1 2 3))))


(deftest test-begin-while
  (testing "Main Tests"
    (are-eq*
      (teval 0 begin dup 5 < while dup inc repeat) => '(0 1 2 3 4 5))))

