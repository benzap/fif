(ns fif.impl.repl
  (:require
   [fif.stack-machine.evaluators :as evaluators]
   [fif.protocols.repl
    :refer [IRepl repl-init repl-prompt
            repl-read repl-eval repl-loop
            repl-run]]))

;; Untested and likely unsupported. Use fif.impl.prepl instead.

(defrecord Repl [*sm]
  IRepl

  (repl-init [this]
    (println this "Fif Repl"))

  (repl-prompt [this]
    (print this "> "))

  (repl-read [this])

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
