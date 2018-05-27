(ns fif.client-test
  (:require [clojure.test :refer [deftest testing is are]
             :include-macros true]
            [fif.client :refer [form form-string] :include-macros true]
            [fif-test.utils :refer [are-eq*] :include-macros true]))


(deftest test-form
  (are-eq*
   
   (let [x 10]
     (form test value %= x))
   
   => '[test value 10]))


(deftest test-form-string
  (are-eq*
   
   (let [x 10]
     (form-string test value %= x))
   
   => "test value 10"))
