(ns fif.stack-machine.exceptions
  "Used to generate stack-error exceptions."
  (:require
   [fif.stack-machine :as stack]
   [fif.stack-machine.error-handling :as error-handling]))


(defn raise-unbounded-mode-argument
  "Used to raise a stack error in situations where a word definition
  used to end a mode does not have a matching start mode."
  [sm]
  (let [word-name (-> sm stack/get-code first)
        errmsg (str "Unbounded mode argument")
        errextra {:end-mode-word word-name}
        errobj (error-handling/stack-error sm errmsg errextra)]
    (error-handling/set-error sm errobj)))
   
