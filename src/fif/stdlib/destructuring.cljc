(ns fif.stdlib.destructuring
  (:require
   [fif.stack-machine :as stack]
   [fif.stack-machine.words :as words]
   [fif.stack-machine.mode :as mode]
   [fif.stack-machine.variable :refer [wrap-local-variable]]
   [fif.stack-machine.exceptions :as exceptions]))


(def arg-destructuring-token '&)


(defn- assign-parameters [sm arg-list]
  (reduce
   (fn [sm [param arg]]
     (let [wfunc (wrap-local-variable arg)]
       (words/set-word sm param wfunc)))
   (concat [sm] (into [] arg-list))))


(defn- check-parameter-types [parameters]
  (some (complement symbol?) parameters))


(defn- pop-stack-n [sm n]
  (loop [sm sm n n]
    (if (<= n 0)
      sm
      (recur (stack/pop-stack sm) (dec n)))))


(defn destructure-op
  [sm]
  (let [[parameters & stack] (stack/get-stack sm)
        arguments (take (count parameters) stack)
        arg-list (zipmap (reverse parameters) arguments)
        sm (-> (pop-stack-n sm (inc (count parameters)))
               stack/dequeue-code)]
    (cond
      ;; Parameters should be presented as a vector
      (not (vector? parameters))
      (exceptions/raise-validation-error
       sm 0 parameters
       "Parameters for destructuring should be presented in the form of a vector")
     
      ;; Parameters must be symbols
      (check-parameter-types parameters)
      (exceptions/raise-validation-error
       sm 0 parameters
       "One or more parameters within the vector form are not symbols")
      
      ;; TODO: check reserved words

      :else
      (assign-parameters sm arg-list))))


(defn import-stdlib-destructuring
  [sm]
  (-> sm

      (words/set-global-word-defn
       arg-destructuring-token destructure-op
       :stdlib? true
       :doc "( arguments & [parameters] -- ) Destructures values on
       the stack defined by `parameters` and places them in local
       variables."
       :group :stdlib.destructuring)))
