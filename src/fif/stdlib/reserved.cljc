(ns fif.stdlib.reserved
  "word symbols that will be reserved, with prevention when being
  generating certain word definitions.")


(def function-begin-definition-word 'fn)
(def function-end-definition-word 'endfn)


(def pointer-subtoken '*)


(def ^:dynamic *reserved-tokens*
  #{
    
    ;; Compile-mode
    function-begin-definition-word
    function-end-definition-word
    
    ;; Variable-mode
    'let 'def

    })
