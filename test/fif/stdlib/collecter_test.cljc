(ns fif.stdlib.collecter-test
  (:require 
   [clojure.test :refer [deftest testing is are]]
   [fif.stdlib.collecter]
   [fif-test.utils :refer [are-eq* teval]]))
            

(deftest test-collecter-into-list
  (testing "<-into! operator with lists"
    (are-eq*
     (teval () <-into! 1 2 3 !) => '((3 2 1))
     (teval (4 5) <-into! 3 1 do i loop !) => '((3 2 1 4 5))
     (teval 1 (4 5) <-into! 3 1 do i loop ! true) => '(1 (3 2 1 4 5) true)))

  (testing "list! operator"
    (are-eq*
     (teval list! 1 2 3 !) => '((3 2 1))
     (teval nil list! 1 2 3 ! true) => '(nil (3 2 1) true)))

  (testing "Nested List Values"
    (are-eq*
     (teval list! 1 2 3 list! 4 5 6 ! !) => '(((6 5 4) 3 2 1)))))


(deftest test-collecter-into-vector
  (testing "<-into! operator with vectors"
    (are-eq*
     (teval [] <-into! 1 2 3 !) => '([1 2 3])
     (teval [1 2] <-into! 3 4 5 !) => '([1 2 3 4 5])
     (teval [] <-into! 3 0 do i loop ! 4) => '([0 1 2 3] 4)))

  (testing "vec! operator"
    (are-eq*
     (teval vec! 1 2 3 !) => '([1 2 3])
     (teval 0 vec! [1 2 3] 4 5 ! 6) => '(0 [[1 2 3] 4 5] 6)))

  (testing "Nested Vector values"
    (are-eq*
     (teval vec! 2 0 do vec! 0 i ! loop !) => '([[0 0] [0 1] [0 2]]))))


(deftest test-collecter-into-map
  (testing "<-into! operator with maps"
    (are-eq*
      (teval {} <-into! [:a 123] [:b 345] !) => '({:a 123 :b 345})
      (teval {:a "prev value"} <-into! [:a 123] [:b 345] !) => '({:a 123 :b 345})
      (teval {} <-into! :a 123 pair :b 345 pair !) => '({:a 123 :b 345})))

  (testing "map! operator"
    (are-eq*
      (teval map! [:a 123] [:b 345] !) => '({:a 123 :b 345})
      (teval map! :a 123 pair :b 345 pair !) => '({:a 123 :b 345}))))


(deftest test-collecter-into-set
  (testing "<-into! operator with sets"
    (are-eq*
     (teval #{} <-into! :dog :cat :mouse !)
     
     => '(#{:dog :cat :mouse})


     (teval #{} <-into! [:dog :cat :mouse] apply !)
     
     => '(#{:dog :cat :mouse})))

  (testing "set! operator"
    (are-eq*
      (teval set! :dog :cat :mouse !)

      => '(#{:dog :cat :mouse})


      (teval set! (:dog :cat :mouse) apply !)
      
      => '(#{:dog :cat :mouse}))))


(deftest test-collecter-into-*
  (testing "collector operators with nesting"
    (are-eq*
     (teval
      fn gen-animals
        set! :cat :dog i 1 = if :mouse then !
      endfn

      vec! 2 1 do
        map!
          :id i pair
          :options
          map!
            [:animals gen-animals] ?
          ! pair
        !
      loop !)

     => '([{:id 1 :options {:animals #{:cat :dog :mouse}}}
           {:id 2 :options {:animals #{:cat :dog}}}])

     (teval
      fn gen-animals
        set! :cat :dog i 1 = if :mouse then !
      endfn

      2 1 do
      gen-animals
      loop)

     => '(#{:cat :dog :mouse} #{:cat :dog}))))
