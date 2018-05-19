(ns fif.stdlib.collection-ops
  "Includes the majority of clojure collection functions, which have
  been ported to fif for use with the same collections.

  All collection operations follow the same argument order:

  Examples:
  (require '[fif.core :as fif])

  ;; Clojure
  (conj [1 2 3] 4) ;; => [1 2 3 4]

  ;; Fif
  (fif/reval [1 2 3] 4 conj) ;; => '([1 2 3 4])

  "
  (:require
   [fif.stack-machine :as stack]
   [fif.stack-machine.words :as words :refer [set-global-word-defn]]
   [fif.def :refer [wrap-function-with-arity
                    wrap-procedure-with-arity]]))


(defn pair
  "Turns the two arguments into a vector of the two arguments."
  [x y]
  [x y])


(defn op-unpair
  "Takes the top value, which is a vector of two-elements, and places
  the two elements on the main stack."
  [sm]
  (let [[x] (stack/get-stack sm)]
    (-> sm
        stack/pop-stack
        (stack/push-stack (first x))
        (stack/push-stack (second x))
        stack/dequeue-code)))


(defn apply-op
  "Takes the top value, which is a collection of values, and places them
  at the front of the code queue."
  [sm]
  (let [[coll] (stack/get-stack sm)]
    (-> sm
        stack/dequeue-code
        stack/pop-stack
        (stack/update-code #(concat %2 %1) coll))))


(defn import-stdlib-collection-ops
  "Imports the collection operators as part of the standard library."
  [sm]
  (-> sm

      (set-global-word-defn
       'apply apply-op
       :stdlib? true
       :doc "( coll - any.. ) Places the contents of the top value onto the stack."
       :group :stdlib.collection)

      (set-global-word-defn
       'assoc (wrap-function-with-arity 3 assoc)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'assoc-in (wrap-function-with-arity 3 assoc-in)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'butlast (wrap-function-with-arity 1 butlast)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'concat (wrap-function-with-arity 2 concat)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'conj (wrap-function-with-arity 2 conj)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'cons (wrap-function-with-arity 2 cons)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'contains? (wrap-function-with-arity 2 contains?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'dedupe (wrap-function-with-arity 1 dedupe)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'dissoc (wrap-function-with-arity 2 dissoc)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'distinct (wrap-function-with-arity 1 distinct)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'drop-last (wrap-function-with-arity 1 drop-last)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'drop-last-n (wrap-function-with-arity 2 drop-last)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'empty (wrap-function-with-arity 1 empty)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'empty? (wrap-function-with-arity 1 empty?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'find (wrap-function-with-arity 2 find)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'first (wrap-function-with-arity 1 first)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'flatten (wrap-function-with-arity 1 flatten)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'fnext (wrap-function-with-arity 1 fnext)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'get (wrap-function-with-arity 2 get)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'get-in (wrap-function-with-arity 2 get-in)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'idrop (wrap-function-with-arity 2 drop)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'interleave (wrap-function-with-arity 2 interleave)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'interleave3 (wrap-function-with-arity 3 interleave)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'interleave4 (wrap-function-with-arity 4 interleave)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'interpose (wrap-function-with-arity 2 interpose)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'into (wrap-function-with-arity 2 into)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'keys (wrap-function-with-arity 1 keys)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'last (wrap-function-with-arity 1 last)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'merge (wrap-function-with-arity 2 merge)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'next (wrap-function-with-arity 1 next)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'nnext (wrap-function-with-arity 1 nnext)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'not-empty (wrap-function-with-arity 1 not-empty)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'nth (wrap-function-with-arity 2 nth)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'nthrest (wrap-function-with-arity 2 nthrest)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'pair (wrap-function-with-arity 2 pair)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'partition (wrap-function-with-arity 2 partition)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'partition-all (wrap-function-with-arity 2 partition-all)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'partition-all3 (wrap-function-with-arity 3 partition-all)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'partition3 (wrap-function-with-arity 3 partition)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'partition4 (wrap-function-with-arity 4 partition)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'peek (wrap-function-with-arity 1 peek)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'pop (wrap-function-with-arity 1 pop)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'rand-nth (wrap-function-with-arity 1 rand-nth)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'random-sample (wrap-function-with-arity 2 random-sample)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'range (wrap-function-with-arity 2 range)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'replace (wrap-function-with-arity 2 replace)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'rest (wrap-function-with-arity 1 rest)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'reverse (wrap-function-with-arity 1 reverse)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'second (wrap-function-with-arity 1 second)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'select-keys (wrap-function-with-arity 2 select-keys)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'seq (wrap-function-with-arity 1 seq)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'set (wrap-function-with-arity 1 set)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'shuffle (wrap-function-with-arity 1 shuffle)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'sort (wrap-function-with-arity 1 sort)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'split-at (wrap-function-with-arity 2 split-at)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'take (wrap-function-with-arity 2 take)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'take-last (wrap-function-with-arity 2 take-last)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'take-nth (wrap-function-with-arity 2 take-nth)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'unpair op-unpair
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'vals (wrap-function-with-arity 1 vals)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'vec (wrap-function-with-arity 1 vec)
       :stdlib? true
       :doc ""
       :group :stdlib)))


