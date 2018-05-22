(ns fif.stdlib.tools
  "Includes stack functionality that can manipulate the entire stack
  machine."
  (:require
   [clojure.string :as str]
   
   [fif.stack-machine.stash :as stack-machine.stash]
   [fif.stack-machine.scope :as stack-machine.scope]
   [fif.stack-machine.words :refer [set-global-word-defn
                                    set-global-meta]]
   [fif.stack-machine.exceptions :as exceptions]
   [fif.stack-machine :as stack]
   [fif.def :as def
    :refer [wrap-function-with-arity
            wrap-procedure-with-arity]
    :include-macros true]))


(defn reset-stack-op
  "Resets the entire stack machine, similar to a soft-reset

  - clears main stack
  - clears the loop return stack
  - resets the scope
  - clears sub-stash
  - clears mode stash
  - clears flags

  Notes:

  - This does not clear the code queue."
  [sm]
  (-> sm
      (stack/clear-stack)
      (stack/clear-ret)
      (stack/clear-temp-macro)
      (stack-machine.scope/clear-scope)
      (stack/set-stash '())
      (stack-machine.stash/clear-stash)
      (stack/clear-flags)
      (stack/dequeue-code)))
      

(defn import-stdlib-stack-tools
  [sm]
  (-> sm

      (set-global-word-defn
       '$reset-stack-machine reset-stack-op
       :stdlib? true
       :group :stdlib.tools
       :doc "soft-resets the stack machine.")

      ))
  
