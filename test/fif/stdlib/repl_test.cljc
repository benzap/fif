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


(deftest test-see
  (are-eq*
   (teval see +) => '()

   (teval see x) => '()

   (teval see 1) => '()

   (teval see 1.2) => '()

   (teval see :test) => '()

   (teval see "string") => '()

   (teval see {:a test}) => '()

   (teval see [1 2 3]) => '()

   (teval see (1 2 3)) => '()

   (teval see x fn x 1 endfn see x) => '()))


(deftest test-see-words
  (are-eq*
   (teval see-words) => '()))


(deftest test-see-user-words
  (are-eq*
   (teval see-user-words) => '()))
