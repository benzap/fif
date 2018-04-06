(ns fif.core
  (:require [fif.stack :as stack]
            [fif.stdlib :as stdlib]
            [fif.compile :refer [add-compile-mode]]))


(defn eval! [stack & args])
  

(-> (stack/new-stack-machine)
    (stack/set-word '+ stdlib/op+)
    (stack/set-word '- stdlib/op-)
    (stack/set-word '. stdlib/dot)
    (add-compile-mode)

    (stack/eval

     1 1 + . ;; First Example
     1 1 + 1 - .
     fn addtwo
       2 +
     endfn

     2 addtwo .))
