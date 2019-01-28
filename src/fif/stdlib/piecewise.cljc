(ns fif.stdlib.piecewise
  "Piecewise operators allow you to re-evaluate values that are on the
  stack.

  This allows you to grab variables that are on the stack to be re-evaluated."
  (:require
   [fif.stack-machine :as stack]
   [fif.stack-machine.words :as words :refer [set-global-word-defn]]
   [fif.def :as def :refer [wrap-function-with-arity
                            wrap-procedure-with-arity]
    :include-macros true]))


(defn piecewise-first
  "(n -- 'n) Queues the first value on the stack back on the code
  stack. Used to dereference variables on the stack, or to reorganize stack values."
  [sm]
  (let [[first] (stack/get-stack sm)]
    (-> sm
        stack/pop-stack
        stack/dequeue-code
        (stack/enqueue-code first))))


(defn piecewise-second
  [sm]
  (let [[second first] (stack/get-stack sm)]
    (-> sm
        stack/pop-stack
        stack/pop-stack
        (stack/push-stack first)
        stack/dequeue-code
        (stack/enqueue-code second))))


(defn piecewise-third
  [sm]
  (let [[third second first] (stack/get-stack sm)]
    (-> sm
        stack/pop-stack
        stack/pop-stack
        stack/pop-stack
        (stack/push-stack second)
        (stack/push-stack first)
        stack/dequeue-code
        (stack/enqueue-code third))))


(defn import-stdlib-piecewise [sm]
  (-> sm
      
      (set-global-word-defn
       '% (wrap-function-with-arity 1 piecewise-first)
       :stdlib? true
       :doc "( n -- 'n ) Pop and re-evaluate the first value on the stack.")

      (set-global-word-defn
       '%1 (wrap-function-with-arity 1 piecewise-first)
       :stdlib? true
       :doc "( n -- 'n ) Pop and re-evaluate the first value on the stack.")

      (set-global-word-defn
       '%2 (wrap-function-with-arity 1 piecewise-second)
       :stdlib? true
       :doc "( n -- 'n ) Pop and re-evaluate the second value on the stack.")

      (set-global-word-defn
       '%3 (wrap-function-with-arity 1 piecewise-third)
       :stdlib? true
       :doc "( n -- 'n ) Pop and re-evaluate the third value on the stack.")))
