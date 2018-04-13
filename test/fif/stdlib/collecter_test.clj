(ns fif.stdlib.collecter-test
  (:require 
   [clojure.test :refer :all]
   [fif.core :as fif :refer [reval]]
   [fif.stdlib.collecter :refer :all]))
            

(deftest test-collecter-into-list
  (is (= '(1 2 3) (reval () <-$ 1 2 3 $<-))))
