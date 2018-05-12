(ns fif.stdlib.tools
  "Includes stack functionality that can manipulate the entire stack
  machine."
  (:require
   [clojure.string :as str]
   [fif.stack-machine :as stack]
   [fif.def :as def
    :refer [wrap-function-with-arity
            wrap-procedure-with-arity]
    :include-macros true]))


(defn reset-stack-op
  "Resets the entire stack machine, similar to a soft-reset

  - clears main stack
  - clears stashes
  - clears flags

  Notes:

  - This does not clear the code queue."
  [sm]
  (-> sm
      (stack/clear-stack)
      (stack/clear-ret)
      (stack/clear-temp-macro)))
      
