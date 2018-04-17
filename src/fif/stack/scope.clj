(ns fif.stack.scope
  "Stack machine functions for manipulating the scope."
  (:require
   [fif.stack :as stack]
   [fif.utils.scope :as utils.scope]))

(defn get-scope [sm]
  (stack/get-scope sm))


(defn set-scope [sm scope]
  (stack/set-scope sm scope))


(defn new-scope
  "Create a new scope environment within the stack machine."
  [sm]
  (let [scope (stack/get-scope sm)]
    (stack/set-scope sm (utils.scope/new-scope scope))))


(defn update-scope
  "Update the current scope environment within the stack machine."
  [sm f & args]
  (let [scope (stack/get-scope sm)]  
    (stack/set-scope sm (apply utils.scope/update-scope scope f args))))


(defn remove-scope
  "Remove the current scope environment from within the stack machine."
  [sm]
  (let [scope (stack/get-scope sm)]
    (stack/set-scope sm (utils.scope/remove-scope scope))))
  

(defn get-in-scope
  "Retrieve within the earliest scope, the given nested attribute."
  ([sm attrs default]
   (let [scope (stack/get-scope sm)]
     (utils.scope/get-in-scope scope attrs default)))
  ([scope attrs] (get-in-scope scope attrs nil)))
