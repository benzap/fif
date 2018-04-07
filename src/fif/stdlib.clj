(ns fif.stdlib
  "Includes all of the primitive standard library functions for
  fif. These functions are stored as a mergeable map to be used by any
  stack machine.

  Notes:

  - Most of the functions listed were taken from the Forth standard library."
  (:refer-clojure :exclude [+])
  (:require [fif.stack :as stack :refer :all]
            [fif.def :refer []]
            [fif.stdlib.conditional :refer [import-stdlib-conditional-mode]]))


(def *stdlib-words (atom {}))


(defn register-stdlib-word! [wname wbody]
  (swap! *stdlib-words assoc wname wbody))


(defn import-stdlib [sm]
  (-> sm
   (update-in [:words] merge @*stdlib-words)
   (import-stdlib-conditional-mode)))


(defn op+
  "(n n -- n) Add top two values of stack"
  [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core/+ i j)]
    (-> sm pop-stack pop-stack (push-stack result))))
(register-stdlib-word! '+ op+)


(defn op-
  "(n n -- n) Subtract top two values of stack"
  [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core/- i j)]
    (-> sm pop-stack pop-stack (push-stack result))))
(register-stdlib-word! '- op-)


(defn op*
  "(n n -- n) Multiply top two values of the stack"
  [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core/* i j)]
    (-> sm pop-stack pop-stack (push-stack result))))
(register-stdlib-word! '* op*)


(defn op-div
  "(n n -- n) Divide top two values of the stack"
  [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core// i j)]
    (-> sm pop-stack pop-stack (push-stack result))))
(register-stdlib-word! '/ op-div)


(defn mod
  "(n n -- n) Get the modulo of the top two values"
  [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core/mod i j)]
    (-> sm pop-stack pop-stack (push-stack result))))
(register-stdlib-word! 'mod mod)


(defn negate
  "(n -- n) Negates the top value"
  [sm]
  (let [[i] (get-stack sm)
        result (clojure.core/- i)]
    (-> sm pop-stack (push-stack result))))
(register-stdlib-word! 'negate negate)


(defn abs
  "(n -- n) Gets the absolute of the top value"
  [sm]
  (let [[i] (get-stack sm)
        result (if (pos? i) i (clojure.core/- i))]
    (-> sm pop-stack (push-stack result))))
(register-stdlib-word! 'abs abs)


(defn op-max
  "(n n -- n) Gets the max value between the top two values"
  [sm]
  (let [[i j] (get-stack sm)
        result (max i j)]
    (-> sm pop-stack pop-stack (push-stack result))))
(register-stdlib-word! 'max op-max)


(defn op-min
  "(n n -- n) Gets teh min value between the top two values"
  [sm]
  (let [[i j] (get-stack sm)
        result (min i j)]
    (-> sm pop-stack pop-stack (push-stack result))))
(register-stdlib-word! 'min op-min)


(defn dup [sm]
  (let [top (-> sm get-stack peek)]
    (push-stack sm top)))
(register-stdlib-word! 'dup dup)


(defn dot [sm]
  (let [top (-> sm get-stack peek)]
    (println top)
    (-> sm
        pop-stack)))
(register-stdlib-word! '. dot)


(defn dot-stack [sm]
  (let [stack (get-stack sm)
        result (apply str "<" (count stack) "> " stack)]
    (println result)
    sm))
(register-stdlib-word! '.s dot-stack)


(defn push-return [sm]
  (let [[i] (get-stack sm)]
    (-> sm pop-stack (push-ret i))))
(register-stdlib-word! '>r push-return)


(defn pop-return [sm]
  (let [[i] (get-ret sm)]
    (-> sm pop-ret (push-stack i))))
(register-stdlib-word! 'r> pop-return)


(defn swap [sm]
  (let [[i j] (get-stack sm)]
    (-> sm pop-stack pop-stack (push-stack i) (push-stack j))))
(register-stdlib-word! 'swap swap)


(defn rot [sm]
  (let [[i j k] (get-stack sm)]
    (-> sm pop-stack pop-stack pop-stack
      (push-stack j) (push-stack k) (push-stack i))))
(register-stdlib-word! 'rot rot)


(defn op-drop [sm]
  (pop-stack sm))
(register-stdlib-word! 'drop op-drop)


(defn nip [sm]
  (let [[i j] (get-stack sm)]
    (-> sm pop-stack pop-stack (push-stack i))))
(register-stdlib-word! 'nip nip)


(defn tuck [sm]
  (let [[i j] (get-stack sm)]
    (-> sm pop-stack pop-stack
        (push-stack i) (push-stack j) (push-stack i))))
(register-stdlib-word! 'tuck tuck)


(defn over [sm]
  (let [[i j] (get-stack sm)]
    (push-stack sm j)))
(register-stdlib-word! 'over over)


(defn roll
  "(v v --) *move* the item at that position to the top"
  [sm]
  (let [stack (get-stack sm)
        pos (peek stack)
        item (nth stack pos)
        new-stack (nthrest stack)]))
(register-stdlib-word! 'roll roll)
