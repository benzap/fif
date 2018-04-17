(ns fif.stdlib.collection-ops-test
  (:require
   [clojure.test :refer :all]
   [fif.stdlib.collection-ops :refer :all]
   [fif-test.utils :refer [teval are-eq*]]))


(deftest test-rest-op
  (is (= '((2 3)) (teval (1 2 3) rest)))
  (is (= '([2 3]) (teval [1 2 3] rest)))
  (is (= '(()) (teval () rest))))


(deftest test-pop-op
  (is (= '((2 3)) (teval (1 2 3) pop)))
  (is (= '([1 2]) (teval [1 2 3] pop))))
  

(deftest test-peek-op
  (is (= '(1) (teval (1 2 3) peek)))
  (is (= '(3) (teval [1 2 3] peek))))
