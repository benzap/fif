(ns fif.stdlib.collection-ops-test
  (:require
   [clojure.test :refer :all]
   [fif.core :refer [reval]]
   [fif.stdlib.collection-ops :refer :all]))


(deftest test-rest-op
  (is (= '((2 3)) (reval (1 2 3) rest)))
  (is (= '([2 3]) (reval [1 2 3] rest)))
  (is (= '(()) (reval () rest))))


(deftest test-pop-op
  (is (= '((2 3)) (reval (1 2 3) pop)))
  (is (= '([1 2]) (reval [1 2 3] pop))))
  

(deftest test-peek-op
  (is (= '(1) (reval (1 2 3) peek)))
  (is (= '(3) (reval [1 2 3] peek))))
