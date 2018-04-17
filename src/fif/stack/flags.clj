(ns fif.stack.flags
  "Functions for manipulating the stack machine flags"
  (:require
   [fif.stack :as stack]))


(defn has-flags?
  "Returns true if the stack machine has active flags."
  [sm]
  (not (empty? (stack/get-flags sm))))
