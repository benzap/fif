(ns fif.impl.repl
  (:require
   [fif.protocols.repl :refer [IRepl]]))


(defrecord Repl [*sm]
  IRepl

  (repl-init [this]
    (println this "Fif Repl"))

  (repl-prompt [this]
    (print this "> "))

  (repl-read [this]
    (read-line))

  (repl-eval [this sform]
    (swap! (:*sm this) evaluators/eval-string sform))

  (repl-loop [this]
    (loop []
      (repl-prompt this)
      (let [sform (repl-read this)]
        (if-not (= sform "bye")
          (do (repl-eval this sform) (recur))
          (println "For now, bye!!")))))

  (repl-run [this]
    (repl-init this)
    (repl-loop this)))


(defn new-repl [sm]
  (map->Repl {:*sm (atom sm)}))
