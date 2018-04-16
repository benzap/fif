(ns fif.stdlib.reserved)


(def function-begin-definition-token 'fn)
(def function-end-definition-token 'endfn)


(def pointer-subtoken '*)


(defn ^:dynamic *reserved-tokens*
  [

   ;; Compile-mode
   function-begin-definition-word
   function-end-definition-word])

   
