(ns fif.impl.repl
  "Clojure Implementation of a basic repl."
  (:require
   [fif.protocols.repl :refer :all]
   [fif.stack-machine.evaluators :as evaluators]))


(defrecord Repl [*sm]
  IRepl

  (repl-init [this]
    (println "Fif Repl")
    (println " 'help' for Help Message,")
    (println " 'bye' to Exit.")
    (flush))

  (repl-prompt [this]
    (print "> ") (flush))

  (repl-read [this]
    (read-line))

  (repl-eval [this sform]
    (swap! (:*sm this) evaluators/eval-string sform))

  (repl-loop [this]
    (loop []
      (repl-prompt this)
      (let [sform (repl-read this)]
        (if-not (= sform "bye")
          (do (repl-eval this sform) (flush) (recur))
          (println "For now, bye!")))))

  (repl-run [this]
    (repl-init this)
    (repl-loop this)))


(defn new-repl [sm]
  (map->Repl {:*sm (atom sm)}))
  
