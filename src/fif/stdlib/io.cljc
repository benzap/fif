(ns fif.stdlib.io
  "Includes IO operations for reading in fif files, or additional text
  files.

  Notes:

  - This is not included in the fif.core/*default-stack* for security reasons.

  - This is used primarily with the 'fif' commandline tool."
  (:require
   [fif.stack-machine :as stack]
   [fif.stack-machine.evaluators :as evaluators]
   [fif.stack-machine.words :as words :refer [set-global-word-defn]]
   [fif.def :as def
    :refer [wrap-function-with-arity
            wrap-procedure-with-arity]
    :include-macros true]))


(defn read-file-op
  [sm]
  (let [[fpath] (stack/get-stack sm)
        s (slurp fpath)
        [sm fif-forms] (evaluators/read-string sm s)]
    (-> sm
        stack/pop-stack
        (stack/push-stack fif-forms)
        stack/dequeue-code)))


(defn load-file-op
  [sm]
  (let [[fpath] (stack/get-stack sm)
        sform (slurp fpath)]
    (-> sm
        stack/pop-stack
        stack/dequeue-code
        (evaluators/eval-string sform))))


(defn import-stdlib-io
  [sm]
  (-> sm
      (set-global-word-defn
       'slurp (wrap-function-with-arity 1 slurp)
       :stdlib? true
       :doc "( fpath -- s ) Reads the file and returns a string."
       :group :stdlib.io)

      (set-global-word-defn
       'spit (wrap-procedure-with-arity 2 spit)
       :stdlib? true
       :doc "( fpath content ) Writes to the file `fpath` overwriting it with `content`."
       :group :stdlib.io)

      (set-global-word-defn
       'spita (wrap-procedure-with-arity 2 #(spit %1 %2 :append true))
       :stdlib? true
       :doc "( fpath content ) Appends to the file `fpath` with `content`."
       :group :stdlib.io)

      (set-global-word-defn
       'read-file read-file-op
       :stdlib? true
       :doc "( fpath -- form ) Reads the file in, and returns it as a form sequence."
       :group :stdlib.io)

      (set-global-word-defn
       'load-file load-file-op
       :stdlib? true
       :doc "( fpath -- ) Loads the given fif script."
       :group :stdlib.io)))
