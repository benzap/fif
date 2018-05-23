(ns fif.stdlib.repl-test
  (:require
   [clojure.test :refer [deftest testing is are]]
   [fif.stdlib.repl]
   [fif.core :refer [reval]]
   [fif-test.utils :refer [teval are-eq*]]))


(deftest test-doc
  (are-eq*
   (teval doc x "Test" *x meta :doc get)
   
   => '("Test")

   (teval doc x "Test" fn x "test" endfn *x meta :doc get)
   
   => '("Test")


   (teval fn x "test" endfn doc x "Test" *x meta :doc get)
   
   => '("Test")))


(deftest test-group
  (are-eq*
   (teval group x :test *x meta :group get)
   
   => '(:test)

   (teval group x :test fn x "test" endfn *x meta :group get)
   
   => '(:test)


   (teval fn x "test" endfn group x :test *x meta :group get)
   
   => '(:test)))


(deftest test-see)


(deftest test-see-words)


(deftest test-see-user-words)
