(ns fif.stdlib.collection-ops-test
  (:require
   [clojure.test :refer [deftest testing is are]]
   [fif.stdlib.collection-ops]
   [fif-test.utils :refer [teval are-eq*]]))


(deftest test-apply-op
  (are-eq*
   (teval (1 2 3) apply)

   => '(1 2 3)


   (teval [1 2 3] apply)

   => '(1 2 3)


   (teval {:a "test"} apply)

   => '([:a "test"])

   
   (teval #{:dog} apply)

   => '(:dog)))


(deftest test-assoc-op
  (are-eq*
   (teval [1 2 3] 0 :test assoc)

   => '([:test 2 3])


   (teval {:a "123" :b nil} :b :test assoc)

   => '({:a "123" :b :test})))


(deftest test-assoc-in-op
  (are-eq*
   (teval [1 2 3] [0] :test assoc-in)

   => '([:test 2 3])

   (teval {:a "123" :b nil} [:b] :test assoc-in)

   => '({:a "123" :b :test})))


(deftest test-butlast-op
  (are-eq*
    (teval [1 2 3] butlast)
 
    => '((1 2))

    (teval [1 2 3] butlast butlast)
 
    => '((1))))


(deftest test-concat-op
  (are-eq*
    (teval [1 2] [3 4] concat)
 
    => '((1 2 3 4))


    (teval [1 2] [3 4] concat [] <> into)
 
    => '([1 2 3 4])))


(deftest test-conj-op
  (are-eq*
    (teval [1 2] :test conj)
 
    => '([1 2 :test])

    (teval (1 2 3 4) :test conj)
 
    => '((:test 1 2 3 4))

    (teval {} :age 29 pair conj)
 
    => '({:age 29})

    (teval #{} :age 29 pair conj)
 
    => '(#{[:age 29]})))


(deftest test-cons-op
  (are-eq*
    (teval 1 (2 3) cons)
 
    => '((1 2 3))))


(deftest test-contains?-op
  (are-eq*
    (teval {:a 1} :a contains?)
 
    => '(true)

    (teval {:a nil} :a contains?)
 
    => '(true)

    (teval {:a 1} :b contains?)
 
    => '(false)

    (teval #{:a} :a contains?)
 
    => '(true)

    (teval #{:a} :b contains?)
 
    => '(false)))


(deftest test-dedupe-op
  (are-eq*
    (teval [1 2 3 3 3 1 1 6] dedupe)

    => '((1 2 3 1 6))))


(deftest test-dissoc-op
  (are-eq*
    (teval {:a 1 :b 2 :c 3} :b dissoc)

    => '({:a 1 :c 3})))


(deftest test-distinct-op
  (are-eq*
    (teval [1 2 1 3 1 4 1 5] distinct)

    => '((1 2 3 4 5))))


(deftest test-drop-last-op
  (are-eq*
    (teval [1 2 3 4] drop-last)

    => '((1 2 3))

    ;; 3-arity version
    (teval 2 [1 2 3 4] drop-last-n)

    => '((1 2))))


(deftest test-empty-op
  (are-eq*
    (teval [1 2 1 3 1 4 1 5] empty)

    => '([])))


(deftest test-empty?-op
  (are-eq*
    (teval [1 2 1 3 1 4 1 5] empty?
           [] empty?)

    => '(false true)))


(deftest test-find-op
  (are-eq*
    (teval {:a 1 :b 2 :c 3} :a find)

    => '([:a 1])))


(deftest test-first-op
  (are-eq*
    (teval [1 2 3] first
           (1 2 3) first)

    => '(1 1)))


(deftest test-flatten-op
  (are-eq*
    (teval [1 2 [3]] flatten
           (1 (2) 3) flatten)

    => '((1 2 3) (1 2 3))))


(deftest test-fnext-op
  (are-eq*
    (teval ([1 2 3] [2 1 3]) fnext)

    => '([2 1 3])))


(deftest test-get-op
  (are-eq*
    (teval [1 2 3] 1 get)

    => '(2)))


(deftest test-get-in-op
  (are-eq*
    (teval [1 2 3] [1] get-in)

    => '(2)))


(deftest test-idrop-op
  (are-eq*
    (teval 1 [1 2 3] idrop)

    => '((2 3))))


(deftest test-interleave-op
  (are-eq*
    (teval [:a :b :c] [1 2 3] interleave)

    => '((:a 1 :b 2 :c 3))))


(deftest test-interpose-op
  (are-eq*
    (teval " " [1 2] interpose)

    => '((1 " " 2))))


(deftest test-into-op
  (are-eq*
    (teval #{} [1 2] into)

    => '(#{1 2})))


(deftest test-keys-op
  (are-eq*
    (teval {:a 1 :b 2 :c 3} keys)
  
    => '((:a :b :c))))


(deftest test-last-op
  (are-eq*
    (teval (:a 1 :b 2 :c 3) last)
  
    => '(3)))


(deftest test-merge-op
  (are-eq*
    (teval {:a 2 :b 2} {:a :test :b 2} merge)
  
    => '({:a :test :b 2})))


(deftest test-next-op
  (are-eq*
    (teval (:a :b :c) next)
  
    => '((:b :c))))


(deftest test-not-empty-op
  (are-eq*
    (teval [1] not-empty) => '([1])
  
    (teval [1 2 5] not-empty) => '([1 2 5])))


(deftest test-nth-op
  (are-eq*
    (teval (:a :b :c) 1 nth)
  
    => '(:b)))


(deftest test-nthrest-op
  (are-eq*
    (teval 0 10 range 5 nthrest)
  
    => '((5 6 7 8 9))))


(deftest test-pair-op
  (are-eq*
    (teval 1 2 pair)
  
    => '([1 2])))


(deftest test-partition-op
  (are-eq*
    (teval 0 9 range 4 <> partition)
  
    => '(((0 1 2 3) (4 5 6 7)))))


(deftest test-partition-all-op
  (are-eq*
    (teval 0 9 range 4 <> partition-all)
  
    => '(((0 1 2 3) (4 5 6 7) (8)))))


(deftest test-peek-op
  (are-eq*
    (teval (1 2 3) peek)
  
    => '(1)

    (teval [1 2 3] peek)
  
    => '(3)))


(deftest test-pop-op
  (are-eq*
    (teval (1 2 3) pop)
  
    => '((2 3))

    (teval [1 2 3] pop)
  
    => '([1 2])))


(deftest test-range-op
  (are-eq*
    (teval 0 3 range)
  
    => '((0 1 2))))


(deftest test-replace-op
  (are-eq*
    (teval [:zeroth :first :second :third :fourth] [0 2 4 0] replace)
  
    => '([:zeroth :second :fourth :zeroth])))


(deftest test-rest-op
  (are-eq*
    (teval (1 2 3) rest)
  
    => '((2 3))))


(deftest test-reverse-op
  (are-eq*
    (teval (1 2 3) reverse)
  
    => '((3 2 1))))


(deftest test-second-op
  (are-eq*
    (teval (1 2 3) second)
  
    => '(2)))


(deftest test-select-keys-op
  (are-eq*
    (teval {:a 1 :b 2} [:a] select-keys)
  
    => '({:a 1})))


(deftest test-seq-op
  (are-eq*
    (teval [1 2] seq)
  
    => '((1 2))))


(deftest test-set-op
  (are-eq*
    (teval (1 1 2 3 2 4 5 5) set)
  
    => '(#{1 2 3 4 5})))


;;TODO: rand-nth
;;TODO: random-sample
;;TODO: shuffle


(deftest test-sort-op
  (are-eq*
    (teval [3 1 2 4] sort)
  
    => '((1 2 3 4))))


(deftest test-split-at-op
  (are-eq*
    (teval 2 [1 2 3 4 5] split-at)
  
    => '([(1 2) (3 4 5)])))


(deftest test-take-op
  (are-eq*
    (teval 2 [1 2 3 4 5] take)
  
    => '((1 2))))


(deftest test-take-last-op
  (are-eq*
    (teval 2 [1 2 3 4 5] take-last)
  
    => '((4 5))))


(deftest test-take-nth-op
  (are-eq*
    (teval 0 10 range 2 <> take-nth)
  
    => '((0 2 4 6 8))))


(deftest test-take-unpair-op
  (are-eq*
    (teval [:a :test] unpair)
  
    => '(:a :test)))


(deftest test-take-vals-op
  (are-eq*
    (teval {:a 1 :b 2 :c 3} vals set)
  
    => '(#{1 2 3})))


(deftest test-take-vec-op
  (are-eq*
    (teval {:a 1} vec)
  
    => '([[:a 1]])))








