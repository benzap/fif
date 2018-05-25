(ns fif.repl
  (:require
   [fif.impl.repl :refer [new-repl]]
   [fif.protocols.repl :refer [repl-run]]))


(defn repl
  [sm]
  (let [r (new-repl sm)]
    (repl-run r)))
