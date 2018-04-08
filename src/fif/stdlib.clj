(ns fif.stdlib
  "Includes all of the primitive standard library functions for
  fif. These functions are stored as a mergeable map to be used by any
  stack machine.

  Notes:

  - Most of the functions listed were taken from the Forth standard library."
  (:refer-clojure :exclude [eval])
  (:require [fif.stack :as stack :refer :all]
            [fif.def :refer []]
            [fif.stdlib.conditional :refer [import-stdlib-conditional-mode]]
            [fif.stdlib.variable :refer [import-stdlib-variable-mode]]
            [fif.stdlib.constant :refer [import-stdlib-constant-mode]]))


(def *stdlib-words (atom {}))


(defn register-stdlib-word! [wname wbody]
  (swap! *stdlib-words assoc wname wbody))


(defn import-stdlib [sm]
  (-> sm
      (update-in [:words] merge @*stdlib-words)
      (import-stdlib-conditional-mode)
      (import-stdlib-variable-mode)
      (import-stdlib-constant-mode)))


(defn op+
  "(n n -- n) Add top two values of stack"
  [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core/+ j i)]
    (-> sm pop-stack pop-stack (push-stack result) dequeue-code)))
(register-stdlib-word! '+ op+)


(defn op-
  "(n n -- n) Subtract top two values of stack"
  [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core/- j i)]
    (-> sm pop-stack pop-stack (push-stack result) dequeue-code)))
(register-stdlib-word! '- op-)


(defn op-plus-1
  [sm]
  (let [[i] (get-stack sm)
        result (inc i)]
    (-> sm pop-stack (push-stack result) dequeue-code)))
(register-stdlib-word! 'inc op-plus-1)


(defn op-minus-1
  [sm]
  (let [[i] (get-stack sm)
        result (dec i)]
    (-> sm pop-stack (push-stack result) dequeue-code)))
(register-stdlib-word! 'dec op-minus-1)


(defn op*
  "(n n -- n) Multiply top two values of the stack"
  [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core/* j i)]
    (-> sm pop-stack pop-stack (push-stack result) dequeue-code)))
(register-stdlib-word! '* op*)


(defn op-div
  "(n n -- n) Divide top two values of the stack"
  [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core// j i)]
    (-> sm pop-stack pop-stack (push-stack result) dequeue-code)))
(register-stdlib-word! '/ op-div)


(defn op-mod
  "(n n -- n) Get the modulo of the top two values"
  [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core/mod j i)]
    (-> sm pop-stack pop-stack (push-stack result) dequeue-code)))
(register-stdlib-word! 'mod op-mod)


(defn negate
  "(n -- n) Negates the top value"
  [sm]
  (let [[i] (get-stack sm)
        result (clojure.core/- i)]
    (-> sm pop-stack (push-stack result) dequeue-code)))
(register-stdlib-word! 'negate negate)


(defn abs
  "(n -- n) Gets the absolute of the top value"
  [sm]
  (let [[i] (get-stack sm)
        result (if (pos? i) i (clojure.core/- i))]
    (-> sm pop-stack (push-stack result) dequeue-code)))
(register-stdlib-word! 'abs abs)


(defn op-max
  "(n n -- n) Gets the max value between the top two values"
  [sm]
  (let [[i j] (get-stack sm)
        result (max j i)]
    (-> sm pop-stack pop-stack (push-stack result) dequeue-code)))
(register-stdlib-word! 'max op-max)


(defn op-min
  "(n n -- n) Gets teh min value between the top two values"
  [sm]
  (let [[i j] (get-stack sm)
        result (min j i)]
    (-> sm pop-stack pop-stack (push-stack result) dequeue-code)))
(register-stdlib-word! 'min op-min)


(defn dup [sm]
  (let [top (-> sm get-stack peek)]
    (-> sm (push-stack top) dequeue-code)))
(register-stdlib-word! 'dup dup)


(defn dot [sm]
  (let [top (-> sm get-stack peek)]
    (print top)
    (-> sm pop-stack dequeue-code)))
(register-stdlib-word! '. dot)


(defn carriage-return [sm]
  (print "\n")
  (-> sm dequeue-code))
(register-stdlib-word! 'cr carriage-return)


(defn dot-stack [sm]
  (let [stack (get-stack sm)
        result (str "<" (count stack) "> ")]
    (print (str "<" (count stack) "> "))
    (prn stack)
    (-> sm dequeue-code)))
(register-stdlib-word! '.s dot-stack)


(defn push-return [sm]
  (let [[i] (get-stack sm)]
    (-> sm pop-stack (push-ret i) dequeue-code)))
(register-stdlib-word! '>r push-return)


(defn pop-return [sm]
  (let [[i] (get-ret sm)]
    (-> sm pop-ret (push-stack i) dequeue-code)))
(register-stdlib-word! 'r> pop-return)


(defn swap [sm]
  (let [[i j] (get-stack sm)]
    (-> sm pop-stack pop-stack (push-stack i) (push-stack j) dequeue-code)))
(register-stdlib-word! 'swap swap)


(defn rot [sm]
  (let [[i j k] (get-stack sm)]
    (-> sm pop-stack pop-stack pop-stack
      (push-stack j) (push-stack k) (push-stack i) dequeue-code)))
(register-stdlib-word! 'rot rot)


(defn op-drop [sm]
  (-> sm pop-stack dequeue-code))
(register-stdlib-word! 'drop op-drop)


(defn nip [sm]
  (let [[i j] (get-stack sm)]
    (-> sm pop-stack pop-stack (push-stack i) dequeue-code)))
(register-stdlib-word! 'nip nip)


(defn tuck [sm]
  (let [[i j] (get-stack sm)]
    (-> sm pop-stack pop-stack
        (push-stack i) (push-stack j) (push-stack i) dequeue-code)))
(register-stdlib-word! 'tuck tuck)


(defn over [sm]
  (let [[i j] (get-stack sm)]
    (-> sm (push-stack j) dequeue-code)))
(register-stdlib-word! 'over over)


(defn roll
  "(v v --) *move* the item at that position to the top"
  [sm]
  (let [stack (get-stack sm)
        pos (peek stack)
        item (nth stack pos)
        new-stack (nthrest stack)]))
(register-stdlib-word! 'roll roll)


(defn op-<
  [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core/< j i)]
   (-> sm
       pop-stack pop-stack (push-stack result) dequeue-code)))
(register-stdlib-word! '< op-<)


(defmacro defstack-arity-2 [name fn]
  `(defn ~name
     [sm#]
     (let [[i# j#] (get-stack sm#)
           result# (~fn j# i#)]
       (-> sm#
           pop-stack pop-stack (push-stack result#) dequeue-code))))


(defstack-arity-2 op-<= <=)
(register-stdlib-word! '<= op-<=)


(defstack-arity-2 op-= =)
(register-stdlib-word! '= op-=)


(defstack-arity-2 op-> >)
(register-stdlib-word! '> op->)


(defstack-arity-2 op->= >=)
(register-stdlib-word! '>= op->=)
