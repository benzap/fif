(ns fif.stdlib.realizer-test
  (:require
   [clojure.test :refer [deftest testing is are]]
   [fif.stdlib.realizer]
   [fif-test.utils :refer [teval are-eq*]]))


(deftest test-realizer
  (are-eq*

   (teval [0 1 2] ?)
   
   => '([0 1 2])

   (teval [2 0 do i loop] ?)
   
   => '([0 1 2])


   (teval [2 0 do i dup 0 = if drop then loop] ?)
   
   => '([1 2])


   (teval (0 1 2) ?)

   => '((0 1 2))


   (teval (2 0 do i loop) ?)

   => '((0 1 2))

   (teval (2 0 do i loop) ? apply)

   => '(0 1 2)


   (teval (2 1 do (2 0 do i loop) ? apply loop) ? apply)

   => '(0 1 2 0 1 2)))
