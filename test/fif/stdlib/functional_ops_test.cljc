(ns fif.stdlib.functional-ops-test
  (:require
   [clojure.test :refer [deftest testing is are]]
   [fif.stdlib.functional-ops]
   [fif-test.utils :refer [teval are-eq*]]))


(deftest test-functional-mode-reduce
  (are-eq*
    (teval *+ [1 2 3 4] reduce)

    => '(10))

  (are-eq*
    (teval
     fn conj2 conj endfn
     *conj2 [[1] 2 3 4] reduce)

    => '([1 2 3 4])))


(deftest test-functional-mode-map
  (are-eq*
    (teval *inc [1 2 3 4] map)

    => '((2 3 4 5))

    (teval
     fn add2 2 + endfn
     *add2 [1 2 3 4] map)

    => '((3 4 5 6))

    (teval
     fn test drop :test endfn
     *test [1 2 3 4] map)

    => '((:test :test :test :test))))


(deftest test-functional-mode-filter
  (are-eq*
    (teval *even? [1 2 3 4] filter)

    => '((2 4))

    (teval *int? [1 2 3.14 4] filter)

    => '((1 2 4))))


(deftest test-functional-mode-all
  (are-eq*
   (teval
    0 5 range
        *inc <> map
        *even? <> filter
        apply)

   => '(2 4))


  (are-eq*
   (teval 0 5 range *inc swap map *even? swap filter apply)

   => '(2 4)


   (teval *even? *inc [4 0 do i loop] ? map filter)
  
   => '([2 4])))
