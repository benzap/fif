(ns fif.stdlib.variable
  (:require [fif.stack :as stack]))


(def arg-variable-token 'variable)
(def variable-mode-flag :variable-mode)


(defn variable-mode
  [sm arg]
  (-> sm
      (stack/set-variable arg nil)
      (stack/pop-flag)))


(defn start-variable
  [sm]
  (-> sm
      (stack/push-flag variable-mode-flag)))


(defn setv [sm]
  (let [[sym val] (stack/get-stack sm)]
    ;; TODO: check if variable exists
    (-> sm
        stack/pop-stack
        stack/pop-stack
        (stack/set-variable sym val))))


(defn getv [sm]
  (let [[sym] (stack/get-stack sm)
        val (-> sm stack/get-variables (get sym))]
    ;; TODO: Check if variable exists
    (-> sm
        stack/pop-stack
        (stack/push-stack val))))


(defn import-stdlib-variable-mode [sm]
  (-> sm
      (stack/set-word arg-variable-token start-variable)
      (stack/set-word '! setv)
      (stack/set-word 'at getv)
      (stack/set-mode variable-mode-flag variable-mode)))
