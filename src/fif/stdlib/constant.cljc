(ns fif.stdlib.constant
  "Allows defining words which return a constant value"
  (:require
   [fif.stack-machine :as stack]
   [fif.stack-machine.words :refer [set-global-word-defn
                                    set-global-meta]]
   [fif.stack-machine.exceptions :as exceptions]))


(def arg-constant-token 'constant)
(def constant-mode-flag :constant-mode)


(defn wrap-word-constant
  "Generates a word function that pops `cval` on the stack"
  [cval]
  (fn [sm]
    (-> sm (stack/push-stack cval) stack/dequeue-code)))


;; TODO; include as reserved word for prevent overwrite.
(defn constant-mode
  "Mode for creating a word definition which pops a constant value onto
  the stack."
  [sm]
  (let [arg (-> sm stack/get-code first)
        [cval] (stack/get-stack sm)]
    (-> sm
        (stack/pop-stack)
        (stack/set-word arg (wrap-word-constant cval))
        (stack/pop-flag)
        stack/dequeue-code)))


(defn start-constant
  "Word definition for going into constant-mode for defining a constant"
  [sm]
  (-> sm
      (stack/push-flag constant-mode-flag)
      stack/dequeue-code))


(defn import-stdlib-constant-mode
  "Stack Machine Import for constant-mode"
  [sm]
  (-> sm

      (set-global-word-defn
       arg-constant-token start-constant
       :stdlib? true
       :group :stdlib.constant
       :doc "constant <name> <value> -- Sets a constant value.")

      (stack/set-mode constant-mode-flag constant-mode)))
    
