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
  - clears sub stash
  - clears mode stash
  - clears flags
  - clears code queue
  "
  [sm]
  (-> sm
      stack/clear-stack
      stack/clear-ret
      stack/clear-temp-macro
      stack/clear-scope
      stack/clear-stash
      stack/clear-mode-stash
      stack/clear-flags
      stack/clear-code))


(defn clear-stack-op
  [sm]
  (-> sm stack/clear-stack stack/dequeue-code))


(defn stack-empty?-op
  [sm]
  (let [bool (empty? (stack/get-stack sm))]
    (-> sm
        (stack/push-stack bool)
        stack/dequeue-code)))


(defn reverse-stack-op
  [sm]
  (let [st (-> sm stack/get-stack reverse)]
    (-> sm
        (stack/set-stack (apply list st))
        stack/dequeue-code)))


(defn get-stack-op
  [sm]
  (let [st (-> sm stack/get-stack)]
    (-> sm
        (stack/push-stack st)
        stack/dequeue-code)))


(defn import-stdlib-stack-tools
  [sm]
  (-> sm

      (set-global-word-defn
       '$reset-stack-machine reset-stack-op
       :stdlib? true
       :group :stdlib.tools
       :doc "soft-resets the stack machine.")

      (set-global-word-defn
       '$clear-stack clear-stack-op
       :stdlib? true
       :group :stdlib.tools
       :doc "Clear the main stack.")

      (set-global-word-defn
       '$empty-stack? stack-empty?-op
       :stdlib? true
       :group :stdlib.tools
       :doc "( -- b ) Returns true, if the main stack is empty.")

      (set-global-word-defn
       '$reverse-stack reverse-stack-op
       :stdlib? true
       :group :stdlib.tools
       :doc "Reverse the main stack.")

      (set-global-word-defn
       '$get-stack get-stack-op
       :stdlib? true
       :group :stdlib.tools
       :doc "( -- stack ) Push a copy of the main stack onto the main stack.")))

      
  
