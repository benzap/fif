(ns fif.stack-machine.verification
  "Functions for ensuring certain guarantees within the stack machine
  before running operations."
  (:require
   [fif.stack-machine :as stack]))


(defn stack-satisfies-arity?
  "Verifies that there are enough arguments on the main stack in order
  to satisfy the provided `num-args` required."
  [sm num-args]
  (-> sm stack/get-stack count (>= num-args)))
