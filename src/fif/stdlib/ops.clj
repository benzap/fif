(ns fif.stdlib.ops
  "Standard Library Word Definitions for common operators

  - Most of the functions listed were taken from the Forth standard library."
  (:refer-clojure :exclude [eval])
  (:require
   [fif.stack :refer :all]))


(defn op+
  "(n n -- n) Add top two values of stack"
  [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core/+ j i)]
    (-> sm pop-stack pop-stack (push-stack result) dequeue-code)))


(defn op-
  "(n n -- n) Subtract top two values of stack"
  [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core/- j i)]
    (-> sm pop-stack pop-stack (push-stack result) dequeue-code)))


(defn op-plus-1
  [sm]
  (let [[i] (get-stack sm)
        result (inc i)]
    (-> sm pop-stack (push-stack result) dequeue-code)))


(defn op-minus-1
  [sm]
  (let [[i] (get-stack sm)
        result (dec i)]
    (-> sm pop-stack (push-stack result) dequeue-code)))


(defn op*
  "(n n -- n) Multiply top two values of the stack"
  [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core/* j i)]
    (-> sm pop-stack pop-stack (push-stack result) dequeue-code)))


(defn op-div
  "(n n -- n) Divide top two values of the stack"
  [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core// j i)]
    (-> sm pop-stack pop-stack (push-stack result) dequeue-code)))


(defn op-mod
  "(n n -- n) Get the modulo of the top two values"
  [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core/mod j i)]
    (-> sm pop-stack pop-stack (push-stack result) dequeue-code)))


(defn negate
  "(n -- n) Negates the top value"
  [sm]
  (let [[i] (get-stack sm)
        result (clojure.core/- i)]
    (-> sm pop-stack (push-stack result) dequeue-code)))


(defn abs
  "(n -- n) Gets the absolute of the top value"
  [sm]
  (let [[i] (get-stack sm)
        result (if (pos? i) i (clojure.core/- i))]
    (-> sm pop-stack (push-stack result) dequeue-code)))


(defn op-max
  "(n n -- n) Gets the max value between the top two values"
  [sm]
  (let [[i j] (get-stack sm)
        result (max j i)]
    (-> sm pop-stack pop-stack (push-stack result) dequeue-code)))


(defn op-min
  "(n n -- n) Gets teh min value between the top two values"
  [sm]
  (let [[i j] (get-stack sm)
        result (min j i)]
    (-> sm pop-stack pop-stack (push-stack result) dequeue-code)))


(defn dup [sm]
  (let [top (-> sm get-stack peek)]
    (-> sm (push-stack top) dequeue-code)))


(defn dot [sm]
  (let [top (-> sm get-stack peek)]
    (print top)
    (-> sm pop-stack dequeue-code)))


(defn carriage-return [sm]
  (print "\n")
  (-> sm dequeue-code))


(defn dot-stack [sm]
  (let [stack (get-stack sm)
        result (str "<" (count stack) "> ")]
    (print (str "<" (count stack) "> "))
    (prn stack)
    (-> sm dequeue-code)))


(defn push-return [sm]
  (let [[i] (get-stack sm)]
    (-> sm pop-stack (push-ret i) dequeue-code)))


(defn pop-return [sm]
  (let [[i] (get-ret sm)]
    (-> sm pop-ret (push-stack i) dequeue-code)))


(defn swap [sm]
  (let [[i j] (get-stack sm)]
    (-> sm pop-stack pop-stack (push-stack i) (push-stack j) dequeue-code)))


(defn rot [sm]
  (let [[i j k] (get-stack sm)]
    (-> sm pop-stack pop-stack pop-stack
      (push-stack j) (push-stack k) (push-stack i) dequeue-code)))


(defn op-drop [sm]
  (-> sm pop-stack dequeue-code))


(defn nip [sm]
  (let [[i j] (get-stack sm)]
    (-> sm pop-stack pop-stack (push-stack i) dequeue-code)))


(defn tuck [sm]
  (let [[i j] (get-stack sm)]
    (-> sm pop-stack pop-stack
        (push-stack i) (push-stack j) (push-stack i) dequeue-code)))


(defn over [sm]
  (let [[i j] (get-stack sm)]
    (-> sm (push-stack j) dequeue-code)))


(defn roll
  "(v v --) *move* the item at that position to the top"
  [sm]
  (let [stack (get-stack sm)
        pos (peek stack)
        item (nth stack pos)
        new-stack (nthrest stack)]))


(defn op-<
  [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core/< j i)]
   (-> sm
       pop-stack pop-stack (push-stack result) dequeue-code)))


(defmacro defstack-arity-2 [name fn]
  `(defn ~name
     [sm#]
     (let [[i# j#] (get-stack sm#)
           result# (~fn j# i#)]
       (-> sm#
           pop-stack pop-stack (push-stack result#) dequeue-code))))


(defstack-arity-2 op-<= <=)
(defstack-arity-2 op-= =)
(defstack-arity-2 op-> >)
(defstack-arity-2 op->= >=)



(defn import-stdlib-ops [sm]
  (-> sm
      (set-word '+ op+)
      (set-word '- op-)
      (set-word 'inc op-plus-1)
      (set-word 'dec op-minus-1)
      (set-word '* op*)
      (set-word '/ op-div)
      (set-word 'mod op-mod)
      (set-word 'negate negate)
      (set-word 'abs abs)
      (set-word 'max op-max)
      (set-word 'min op-min)
      (set-word 'dup dup)
      (set-word '. dot)
      (set-word 'cr carriage-return)
      (set-word '.s dot-stack)
      (set-word '>r push-return)
      (set-word 'r> pop-return)
      (set-word 'swap swap)
      (set-word 'rot rot)
      (set-word 'drop op-drop)
      (set-word 'nip nip)
      (set-word 'tuck tuck)
      (set-word 'over over)
      (set-word 'roll roll)
      (set-word '< op-<)
      (set-word '<= op-<=)
      (set-word '= op-=)
      (set-word '> op->)
      (set-word '>= op->=)))
