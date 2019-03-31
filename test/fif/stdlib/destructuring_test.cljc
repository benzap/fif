(ns fif.stdlib.destructuring-test
  (:require
   [clojure.test :refer [deftest testing is are]]
   [fif.stdlib.destructuring]
   [fif-test.utils :refer [teval are-eq*]]))


(deftest test-destructuring
  (are-eq*

   (teval
    1 2 3 [a b c] &
    a b c)

   => '(1 2 3)

   
   (teval
    nil 1 2 [a b c] &
    a b c)
  
   => '(nil 1 2)


   (teval
    1 2 3 [a b] &
    a b)
  
   => '(1 2 3)))



