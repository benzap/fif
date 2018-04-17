(ns fif.stack-machine.flags
  "Functions for manipulating the stack machine flags"
  (:require
   [fif.stack-machine :as stack]))


(defn has-flags?
  "Returns true if the stack machine has active flags."
  [sm]
  (not (empty? (stack/get-flags sm))))
