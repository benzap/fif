(ns fif.protocols.repl)


(defprotocol IRepl
  (repl-init [this])
  (repl-prompt [this])
  (repl-read [this])
  (repl-eval [this form])
  (repl-print [this args])
  (repl-loop [this])
  (repl-run [this]))
