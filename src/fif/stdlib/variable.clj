(ns fif.stdlib.variable
  "Includes the variable-mode, for creating mutable variables within a
  stack machine."
  (:require
   [fif.stack-machine :as stack]
   [fif.stack-machine.words :as stack.words]))


(def arg-variable-token 'def)
(def variable-mode-flag :variable-mode)


(defn wrap-global-variable
  [value]
  (fn [sm]
    (-> sm
        (stack/push-stack value)
        stack/dequeue-code)))


(defn variable-mode
  "Variable mode is used to define a variable, which can then be
  manipulated with two provided methods."
  [sm]
  (let [arg (-> sm stack/get-code first)]
    (cond
      (symbol? arg)
      (-> sm
          (stack/push-stack arg)
          stack/dequeue-code)
      :else
      (let [vname (-> sm stack/get-stack peek)]
        (-> sm
            (stack.words/set-global-word vname (wrap-global-variable arg))
            stack/pop-stack
            (stack/pop-flag)
            stack/dequeue-code)))))


(defn start-variable
  "Puts the stack machine into variable-mode"
  [sm]
  (-> sm
      (stack/push-flag variable-mode-flag)
      stack/dequeue-code))


(defn setg
  "Word function used to set a variable to a provided value"
  [sm]
  (let [[val sym] (stack/get-stack sm)]
    ;; TODO: check if variable exists
    (-> sm
        stack/pop-stack
        stack/pop-stack
        (stack.words/set-global-word sym (wrap-global-variable val))
        stack/dequeue-code)))


(defn import-stdlib-variable-mode
  "Stack Machine Import for variable-mode"
  [sm]
  (-> sm
      (stack/set-word arg-variable-token start-variable)
      (stack/set-word 'setg setg)
      (stack/set-mode variable-mode-flag variable-mode)))
