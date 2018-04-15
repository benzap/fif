(ns fif.stdlib.collecter-test
  (:require 
   [clojure.test :refer :all]
   [fif.core :as fif :refer [reval dbg-eval]]
   [fif.stack :as stack]
   [fif.stdlib.collecter :refer :all]))
            

(deftest test-collecter-into-list
  (is (= '((3 2 1)) (reval () <-into! 1 2 3 !)))
  (is (= '((3 2 1 4 5)) (reval (4 5) <-into! 3 1 do i loop !)))
  (is (= '((3 2 1)) (reval list! 1 2 3 !))))


(deftest test-collecter-into-vector
  (is (= '([1 2 3]) (reval [] <-into! 1 2 3 !)))
  (is (= '([1 2 3 4 5]) (reval [1 2] <-into! 3 4 5 !)))
  (is (= '([3 4 5]) (reval vec! 3 4 5 !))))


