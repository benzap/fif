(ns fif.client-test
  (:require [clojure.test :refer [deftest testing is are]
             :include-macros true]
            [fif.client :refer [form-fn form] :include-macros true]
            [fif-test.utils :refer [are-eq*] :include-macros true]))


(deftest test-form-fn
  (are-eq*
   
   (let [x 10]
     (form-fn 'test 'value '%= 'x))

   => '[test value 10]))


#_(deftest test-form
    (are-eq*

     (let [x 10]
       (form test value %= x))

     => '[test value 10]))
