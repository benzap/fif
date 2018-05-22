(ns fif.stack-machine.exceptions
  "Used to generate stack-error exceptions."
  (:require
   [fif.stack-machine :as stack]
   [fif.stack-machine.error-handling :as error-handling]))


(defn raise-unbounded-mode-argument
  "Used to raise a stack error in situations where a word definition is
  used improperly. Usually for words used to end a mode that don't
  have a matching start mode definition."
  [sm]
  (let [word-name (-> sm stack/get-code first)
        errmsg (str "Unbounded mode argument")
        errextra {:mode-word word-name}
        errobj (error-handling/stack-error sm errmsg errextra)]
    (error-handling/handle-stack-error sm errobj)))

