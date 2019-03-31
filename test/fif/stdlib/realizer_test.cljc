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

   => '(0 1 2 0 1 2) 


   (teval  def a 10 def b 12 {:a a :b b} ?)

   => '({:a 10 :b 12})


   (teval  {:a [1 2] :b (2 2 +)} ?)

   => '({:a [1 2] :b 4})


   (teval  {:a [1 2] :b (2 2 2 +)} ?)

   => '({:a [1 2] :b (2 4)})

   (teval 1 ?)

   => '(1)

   (teval 1 ??)

   => '(1)

   (teval
    def a 1
    def b 2
    def m {:a a :b b}

    [{:a a
      :b b
      :c [a b m]}]
    ??)
          

   => '([{:a 1 :b 2 :c [1 2 {:a 1 :b 2}]}])))
