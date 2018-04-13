(ns fif.stdlib.collection-ops-test
  (:require
   [clojure.test :refer :all]
   [fif.core :refer [reval]]
   [fif.stdlib.collection-ops :refer :all]))


(deftest test-rest-op
  (is (= '((2 3)) (reval (1 2 3) rest)))
  (is (= '([2 3]) (reval [1 2 3] rest)))
  (is (= '(()) (reval () rest))))


#_(reval [1 2] unpair)
