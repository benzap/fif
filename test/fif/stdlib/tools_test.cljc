(ns fif.stdlib.tools-test
  (:require
   [clojure.test :refer [deftest testing is are]]
   [fif.stdlib.tools]
   [fif.core :refer [reval]]
   [fif-test.utils :refer [teval are-eq*]]))


(deftest test-clear-stack
  (are-eq*
   (teval 2 2 2 2 2 2 2 $clear-stack)
   
   => '()

   (teval $clear-stack)
   
   => '()

   (teval 2 2 $clear-stack 2 2 +)
   
   => '(4)))


(deftest test-stack-empty?
  (are-eq*
   (teval 2 2 2 2 2 2 2 $empty-stack?)
   
   => '(2 2 2 2 2 2 2 false)

   (teval $empty-stack?)
   
   => '(true)

   (teval 2 2 + drop $empty-stack? 2)
   
   => '(true 2)))


(deftest test-reset-stack-machine
  (are-eq*
   (teval $reset-stack-machine 1 1 +)
   
   => '()))


(deftest test-reverse-stack
  (are-eq*
   (teval 1 2 3 4 $reverse-stack)
   
   => '(4 3 2 1)


   (teval 4 3 2 1 $reverse-stack 5)
   
   => '(1 2 3 4 5)


   (teval $reverse-stack 5)
   
   => '(5)))
