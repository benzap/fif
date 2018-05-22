(ns fif.stack-machine.scope
  "Stack machine functions for manipulating the scope."
  (:require
   [fif.stack-machine :as stack]
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


(defn clear-scope
  [sm]
  (stack/set-scope sm (utils.scope/new-scope)))


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
  "Retrieve within the latest scope, the given nested attribute."
  ([sm attrs default]
   (let [scope (stack/get-scope sm)]
     (utils.scope/get-in-scope scope attrs default)))
  ([scope attrs] (get-in-scope scope attrs nil)))


(defn update-global-scope
  "Update the current scope environment within the stack machine."
  [sm f & args]
  (let [scope (stack/get-scope sm)]  
    (stack/set-scope sm (apply utils.scope/update-global-scope scope f args))))


(defn get-in-global-scope
  "Retrieves from the earliest scope"
  ([sm attrs default]
   (let [fscope (-> sm stack/get-scope first)]
     (get-in fscope attrs default)))
  ([sm attrs] (get-in-global-scope sm attrs nil)))


(defn get-merged-scope
  [sm]
  (let [scope (stack/get-scope sm)]
    (utils.scope/get-merged-scope scope)))
