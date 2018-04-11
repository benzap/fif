(ns fif.core
  (:refer-clojure :exclude [eval])
  (:require [fif.stack :as stack]
            [fif.stdlib :refer [import-stdlib]]))


(def get-code stack/get-code)
(def get-stack stack/get-stack)
(def get-ret stack/get-ret)
(def get-flags stack/get-flags)


(def ^:dynamic
  *default-stack*
  "Default Stack Machine, containing all of the standard
  libraries. Can be used with `with-stack` in order to rebind for
  `eval`, `seval` and `reval`"
  (-> (stack/new-stack-machine)
      import-stdlib))


(defmacro with-stack
  "Used to rebind the `*default-stack*` for use with `eval`, `seval` and
  `reval`"
  [sm & body]
  `(binding [*default-stack* ~sm]
     ~@body))


(defn eval-fn
  "Evaluate the sequence of args within the fif stackmachine, and return
  the stackmachine as a result."
  [args]
  (-> *default-stack*
      (stack/eval-fn args)))


(defmacro eval
  "Evaluate the values provided in `body` within the fif
   stackmachine, and return the stackmachine"
  [& body]
  `(eval-fn (quote ~body)))


(defmacro seval
  "Evaluate the values provided in `body`, and return the main stack
  after completion.

  Notes:

  - You will likely want to use fif.core/reval, since it returns the
  main stack in a more pleasing orientation."
  [& body]
  `(-> (eval-fn (quote ~body)) stack/get-stack))


(defmacro reval
  "Evaluate the values provided in `body` within the fif stackmachine,
  and return the main stack as a result."
  [& body]
  `(-> (eval-fn (quote ~body)) stack/get-stack reverse))


(defn eval-string
  "Evaluates the given string as a stream of EDN values within the fif
  stackmachine, and returns the stackmachine after evaluation."
  [s]
  (-> *default-stack* (stack/eval-string s)))


(defmacro dbg-eval
  "Debugging tool. By default enables the step inhibitor"
  [opts & body]
  `(let [step-max# (get ~opts :step-max 100)]
     (-> *default-stack*
         (stack/set-step-max step-max#)
         (stack/eval-fn (quote ~body)))))
