(ns fif.stdlib.reader
  (:require
   [fif.stack-machine :as stack]
   [fif.stack-machine.evaluators :as evaluators]
   [fif.stack-machine.words :as words :refer [set-global-word-defn]]
   [fif.def :as def
    :refer [wrap-function-with-arity
            wrap-procedure-with-arity]
    :include-macros true]))


(defn read-string-op
  [sm]
  (let [[s] (stack/get-stack sm)
        [sm fif-forms] (evaluators/read-string sm s)]
    (-> sm
        stack/pop-stack
        (stack/push-stack fif-forms)
        stack/dequeue-code)))
  

(defn import-stdlib-reader [sm]
  (-> sm

      (set-global-word-defn
       'read-string read-string-op
       :doc "( s -- vec-form ) Reads the string as an edn structure"
       :group :stdlib.reader
       :stdlib? true)))
