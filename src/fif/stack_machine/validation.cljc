(ns fif.stack-machine.validation
  "Includes functions for validating stack-machine word operations."
  (:require
   [fif.stack-machine :as stack]
   [fif.stack-machine.error-handling :as error-handling]
   [fif.stack-machine.exceptions :as exceptions]))


(defn validate-stack-arg
  [sm index validation-fn errmsg]
  (let [arg (-> sm stack/get-stack (nth index))]
    (if (validation-fn arg)
      sm
      (exceptions/raise-validation-error sm index arg errmsg))))


(defn arg-sym?
  [sm index]
  (validate-stack-arg sm index symbol? (str "Stack Argument '" index "' is not a symbol")))


(defn arg-string?
  [sm index]
  (validate-stack-arg sm index string? (str "Stack Argument '" index "' is not a string")))
