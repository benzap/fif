(ns fif.stdlib.variable
  "Includes the variable-mode, for creating mutable variables within a
  stack machine."
  (:require [fif.stack :as stack]))


(def arg-variable-token 'variable)
(def variable-mode-flag :variable-mode)


(defn variable-mode
  "Variable mode is used to define a variable, which can then be
  manipulated with two provided methods."
  [sm]
  (let [arg (-> sm stack/get-code first)]
    (-> sm
        (stack/set-variable arg nil)
        (stack/pop-flag)
        stack/dequeue-code)))


(defn start-variable
  "Puts the stack machine into variable-mode"
  [sm]
  (-> sm
      (stack/push-flag variable-mode-flag)
      stack/dequeue-code))


(defn setv
  "Word function used to set a variable to a provided value"
  [sm]
  (let [[sym val] (stack/get-stack sm)]
    ;; TODO: check if variable exists
    (-> sm
        stack/pop-stack
        stack/pop-stack
        (stack/set-variable sym val)
        stack/dequeue-code)))


(defn getv
  "Word function used to retrieve the value from a provided variable"
  [sm]
  (let [[sym] (stack/get-stack sm)
        val (-> sm stack/get-variables (get sym))]
    ;; TODO: Check if variable exists
    (-> sm
        stack/pop-stack
        (stack/push-stack val)
        stack/dequeue-code)))


(defn import-stdlib-variable-mode
  "Stack Machine Import for variable-mode"
  [sm]
  (-> sm
      (stack/set-word arg-variable-token start-variable)
      (stack/set-word '! setv)
      (stack/set-word 'at getv)
      (stack/set-mode variable-mode-flag variable-mode)))
