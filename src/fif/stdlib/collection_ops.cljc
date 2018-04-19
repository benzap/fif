(ns fif.stdlib.collection-ops
  (:require
   [fif.stack-machine :as stack]
   [fif.def :refer [wrap-function-with-arity
                    wrap-procedure-with-arity]]))


(defn pair [x y] [x y])


(defn op-unpair [sm]
  (let [[x] (stack/get-stack sm)]
    (-> sm
        stack/pop-stack
        (stack/push-stack (first x))
        (stack/push-stack (second x))
        stack/dequeue-code)))


(defn apply-op [sm]
  (let [[coll] (stack/get-stack sm)]
    (-> sm
        stack/dequeue-code
        stack/pop-stack
        (stack/update-code #(concat %2 %1) coll))))


(defn import-stdlib-collection-ops
  [sm]
  (-> sm
      (stack/set-word 'apply apply-op)
      (stack/set-word 'assoc (wrap-function-with-arity 3 assoc))
      (stack/set-word 'assoc-in (wrap-function-with-arity 3 assoc-in))
      (stack/set-word 'butlast (wrap-function-with-arity 1 butlast))
      (stack/set-word 'concat (wrap-function-with-arity 2 concat))
      (stack/set-word 'conj (wrap-function-with-arity 2 conj))
      (stack/set-word 'cons (wrap-function-with-arity 1 cons))
      (stack/set-word 'contains? (wrap-function-with-arity 2 contains?))
      (stack/set-word 'dedupe (wrap-function-with-arity 1 dedupe))
      (stack/set-word 'dissoc (wrap-function-with-arity 2 dissoc))
      (stack/set-word 'distinct (wrap-function-with-arity 1 distinct))
      (stack/set-word 'drop-last (wrap-function-with-arity 2 drop-last))
      (stack/set-word 'empty (wrap-function-with-arity 1 empty))
      (stack/set-word 'empty? (wrap-function-with-arity 1 empty?))
      (stack/set-word 'find (wrap-function-with-arity 2 find))
      (stack/set-word 'first (wrap-function-with-arity 1 first))
      (stack/set-word 'flatten (wrap-function-with-arity 1 flatten))
      (stack/set-word 'fnext (wrap-function-with-arity 1 fnext))
      (stack/set-word 'get (wrap-function-with-arity 2 get))
      (stack/set-word 'get-in (wrap-function-with-arity 2 get-in))
      (stack/set-word 'idrop (wrap-function-with-arity 2 drop))
      (stack/set-word 'interleave (wrap-function-with-arity 2 interleave))
      (stack/set-word 'interpose (wrap-function-with-arity 2 interpose))
      (stack/set-word 'into (wrap-function-with-arity 2 into))
      (stack/set-word 'keys (wrap-function-with-arity 1 keys))
      (stack/set-word 'last (wrap-function-with-arity 1 last))
      (stack/set-word 'merge (wrap-function-with-arity 2 merge))
      (stack/set-word 'next (wrap-function-with-arity 1 next))
      (stack/set-word 'nnext (wrap-function-with-arity 1 nnext))
      (stack/set-word 'not-empty (wrap-function-with-arity 1 not-empty))
      (stack/set-word 'nth (wrap-function-with-arity 2 nth))
      (stack/set-word 'nthrest (wrap-function-with-arity 2 nthrest))
      (stack/set-word 'pair (wrap-function-with-arity 2 pair))
      (stack/set-word 'partition (wrap-function-with-arity 2 partition))
      (stack/set-word 'partition-all (wrap-function-with-arity 2 partition-all))
      (stack/set-word 'partition-all3 (wrap-function-with-arity 3 partition-all))
      (stack/set-word 'partition3 (wrap-function-with-arity 3 partition))
      (stack/set-word 'partition4 (wrap-function-with-arity 4 partition))
      (stack/set-word 'peek (wrap-function-with-arity 1 peek))
      (stack/set-word 'pop (wrap-function-with-arity 1 pop))
      (stack/set-word 'rand-nth (wrap-function-with-arity 1 rand-nth))
      (stack/set-word 'random-sample (wrap-function-with-arity 2 random-sample))
      (stack/set-word 'range (wrap-function-with-arity 2 range))
      (stack/set-word 'replace (wrap-function-with-arity 2 replace))
      (stack/set-word 'rest (wrap-function-with-arity 1 rest))
      (stack/set-word 'reverse (wrap-function-with-arity 1 reverse))
      (stack/set-word 'second (wrap-function-with-arity 1 second))
      (stack/set-word 'select-keys (wrap-function-with-arity 2 select-keys))
      (stack/set-word 'seq (wrap-function-with-arity 1 seq))
      (stack/set-word 'set (wrap-function-with-arity 1 set))
      (stack/set-word 'shuffle (wrap-function-with-arity 1 shuffle))
      (stack/set-word 'sort (wrap-function-with-arity 1 sort))
      (stack/set-word 'split-at (wrap-function-with-arity 2 split-at))
      (stack/set-word 'take (wrap-function-with-arity 2 take))
      (stack/set-word 'take-last (wrap-function-with-arity 2 fnext))
      (stack/set-word 'take-nth (wrap-function-with-arity 2 take-nth))
      (stack/set-word 'unpair op-unpair)
      (stack/set-word 'vals (wrap-function-with-arity 1 vals))
      (stack/set-word 'vec (wrap-function-with-arity 1 vec))))

