(ns fif.stdlib.collection-ops
  (:refer-clojure :exclude [eval])
  (:require
   [fif.stack-machine :refer :all]
   [fif.def :refer :all]))


(defn pair [x y] [x y])


(defn op-unpair [sm]
  (let [[x] (get-stack sm)]
    (-> sm
        pop-stack
        (push-stack (first x))
        (push-stack (second x))
        dequeue-code)))


(defn import-stdlib-collection-ops
  [sm]
  (-> sm
      (set-word 'rest (wrap-function-with-arity 1 rest))
      (set-word 'last (wrap-function-with-arity 1 last))
      (set-word 'rand-nth (wrap-function-with-arity 1 rand-nth))
      (set-word 'nthrest (wrap-function-with-arity 2 nthrest))
      (set-word 'next (wrap-function-with-arity 1 next))
      (set-word 'fnext (wrap-function-with-arity 1 fnext))
      (set-word 'nnext (wrap-function-with-arity 1 nnext))
      (set-word 'ddrop (wrap-function-with-arity 2 drop))
      (set-word 'pop (wrap-function-with-arity 1 pop))
      (set-word 'peek (wrap-function-with-arity 1 peek))
      (set-word 'into (wrap-function-with-arity 2 into))
      (set-word 'first (wrap-function-with-arity 1 first))
      (set-word 'second (wrap-function-with-arity 1 second))
      (set-word 'empty? (wrap-function-with-arity 1 empty?))
      (set-word 'empty (wrap-function-with-arity 1 empty))
      (set-word 'not-empty (wrap-function-with-arity 1 not-empty))
      (set-word 'nth (wrap-function-with-arity 2 nth))
      (set-word 'conj (wrap-function-with-arity 2 conj))
      (set-word 'pair (wrap-function-with-arity 2 pair))
      (set-word 'unpair op-unpair)
      (set-word 'assoc (wrap-function-with-arity 3 assoc))
      (set-word 'assoc-in (wrap-function-with-arity 3 assoc-in))
      (set-word 'merge (wrap-function-with-arity 2 merge))
      (set-word 'select-keys (wrap-function-with-arity 2 select-keys))
      (set-word 'get (wrap-function-with-arity 2 get))
      (set-word 'get-in (wrap-function-with-arity 2 get-in))
      (set-word 'contains? (wrap-function-with-arity 2 contains?))
      (set-word 'find (wrap-function-with-arity 2 find))
      (set-word 'dissoc (wrap-function-with-arity 2 dissoc))
      (set-word 'keys (wrap-function-with-arity 1 keys))
      (set-word 'vals (wrap-function-with-arity 1 vals))
      (set-word 'cons (wrap-function-with-arity 1 cons))
      (set-word 'dedupe (wrap-function-with-arity 1 dedupe))
      (set-word 'distinct (wrap-function-with-arity 1 distinct))
      (set-word 'take (wrap-function-with-arity 2 take))
      (set-word 'take-nth (wrap-function-with-arity 2 take-nth))
      (set-word 'butlast (wrap-function-with-arity 1 butlast))
      (set-word 'drop-last (wrap-function-with-arity 2 drop-last))
      (set-word 'partition (wrap-function-with-arity 2 partition))
      (set-word 'partition3 (wrap-function-with-arity 3 partition))
      (set-word 'partition4 (wrap-function-with-arity 4 partition))
      (set-word 'partition-all (wrap-function-with-arity 2 partition-all))
      (set-word 'partition-all3 (wrap-function-with-arity 3 partition-all))
      (set-word 'split-at (wrap-function-with-arity 2 split-at))
      (set-word 'reverse (wrap-function-with-arity 1 reverse))
      (set-word 'replace (wrap-function-with-arity 2 replace))
      (set-word 'sort (wrap-function-with-arity 1 sort))
      (set-word 'shuffle (wrap-function-with-arity 1 shuffle))
      (set-word 'flatten (wrap-function-with-arity 1 flatten))
      (set-word 'take-last (wrap-function-with-arity 2 fnext))
      (set-word 'random-sample (wrap-function-with-arity 2 random-sample))
      (set-word 'concat (wrap-function-with-arity 2 concat))
      (set-word 'interleave (wrap-function-with-arity 2 interleave))
      (set-word 'interpose (wrap-function-with-arity 2 interpose))
      (set-word 'vec (wrap-function-with-arity 1 vec))
      (set-word 'set (wrap-function-with-arity 1 set))
      (set-word 'seq (wrap-function-with-arity 1 seq))
      (set-word 'range (wrap-function-with-arity 2 range))))

