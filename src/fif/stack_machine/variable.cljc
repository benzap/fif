(ns fif.stack-machine.variable
  (:require
   [fif.stack-machine :as stack]))


(defn wrap-global-variable
  "Converts a given value into a stack-machine word definition suitable
  for use as a word variable.
  
  Notes:

  - It dequeue's the code stack, so it's only suitable for defining a
  word definition as a variable."
  [value]
  (fn [sm]
    (-> sm
        stack/dequeue-code
        (stack/update-code #(concat %2 %1) [value]))))


(defn wrap-local-variable
  [value]
  (fn [sm]
    (-> sm
        stack/dequeue-code
        (stack/update-code #(concat %2 %1) [value]))))
