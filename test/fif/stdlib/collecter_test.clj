(ns fif.stdlib.collecter-test
  (:require 
   [clojure.test :refer :all]
   [fif.core :as fif :refer [reval dbg-eval]]
   [fif.stack :as stack]
   [fif.stdlib.collecter :refer :all]))
            

(deftest test-collecter-into-list
  (is (= '((3 2 1)) (reval () <-$ 1 2 3 $<-)))
  (is (= '((3 2 1 4 5)) (reval (4 5) <-$ 3 1 do i loop $<-))))


(deftest test-collecter-into-vector
  (is (= '([1 2 3]) (reval [] <-$ 1 2 3 $<-)))
  (is (= '([1 2 3 4 5]) (reval [1 2] <-$ 3 4 5 $<-))))


#_(-> (dbg-eval {:step-max 50} [1 2 3] <-$ 5 4 do i loop $<-)
      (stack/get-stack)
      reverse)

(reval
 [] <-$
      [] <-$ 1 2 3 $<-
      () <-$ 1 2 3 $<- 
      #{} <-$ :a :b :c $<-
      {:c 123} <-$ [:a 1] [:b 2] $<-
      $<-)

(reval
 macro gen-end _$ $<- $_ endmacro
 macro gen-list _$ () <-$ $_ endmacro
 macro gen-dict _$ {} <-$ $_ endmacro
 macro gen-vec _$ [] <-$ $_ endmacro

 gen-dict [:x 1] gen-end)
