(ns fif.stdlib.s-expression
  (:require
   [fif.stack-machine :as stack]
   [fif.stack-machine.words :as words]))


(defn- swap-to-rpn [coll]
  (cond
    (sequential? coll)
    (let [word (first coll)
          args (rest coll)]
      (concat args [word]))
    :else [coll]))


(defn s-expression-op
  [sm]
  (let [[expr] (stack/get-stack sm)
        code (-> sm stack/dequeue-code stack/get-code)
        updated-code (concat (swap-to-rpn expr) code)]
    (-> sm
        stack/pop-stack
        (stack/set-code updated-code))))


(defn import-stdlib-s-expression
  [sm]
  (-> sm
  
      (words/set-global-word-defn
       '$ s-expression-op
       :stdlib? true
       :doc "( s-expression -- ) Takes an s-expression formatted word with arguments"
       :group :stdlib.destructuring)))
