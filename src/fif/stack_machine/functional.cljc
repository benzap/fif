(ns fif.stack-machine.functional
  (:require
   [fif.stack-machine :as stack]))


(defn wrap-seq-function
  "Turns the given sequence into a word function.

  Notes:

  - it does not dequeue the code stack.

  - this is considered to be somewhat like a lambda function, and
  lacks its own scope."
  [scoll]

  ;; TODO: check if it's a seq, and raise an error
  (fn [sm]
    (-> sm
        (stack/update-code #(concat %2 %1) scoll))))
