(ns fif.stdlib.collection-ops
  (:refer-clojure :exclude [eval])
  (:require
   [fif.stack :refer :all]
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
      (set-word 'pop (wrap-function-with-arity 1 pop))
      (set-word 'peek (wrap-function-with-arity 1 peek))
      (set-word 'into (wrap-function-with-arity 2 into))
      (set-word 'first (wrap-function-with-arity 1 first))
      (set-word 'second (wrap-function-with-arity 1 second))
      (set-word 'nth (wrap-function-with-arity 2 nth))
      (set-word 'conj (wrap-function-with-arity 2 conj))
      (set-word 'pair (wrap-function-with-arity 2 pair))
      (set-word 'unpair op-unpair)))
