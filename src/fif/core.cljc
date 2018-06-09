(ns fif.core
  (:refer-clojure :exclude [eval])
  (:require
   [fif.stack-machine :as stack]
   [fif.stack-machine.evaluators :as stack.evaluators]
   [fif.stack-machine.stash :as stack-machine.stash]
   [fif.stack-machine.error-handling :refer [default-system-error-handler
                                             default-stack-error-handler]]
   [fif.stdlib :refer [import-stdlib]]
   [fif.impl.stack-machine :refer [new-stack-machine]]
   [fif.impl.prepl]
   [fif.repl])
  #?(:clj (:gen-class)))


(def get-stack stack/get-stack)


(def ^:dynamic
  *default-stack*
  "Default Stack Machine, containing all of the standard
  libraries. Can be used with `with-stack` in order to rebind for
  `eval`, `seval` and `reval`"
  (-> (new-stack-machine)
      (stack/set-system-error-handler default-system-error-handler)
      (stack/set-stack-error-handler default-stack-error-handler)
      import-stdlib))


(defn repl
  "Command-line Repl for either the *default-stack*, or a provided
  stack-machine."
  ([sm] (fif.repl/repl sm))
  ([] (repl *default-stack*)))


(def prepl-eval
  "Programmable Repl Evaluation Function, for creating a full-fledged
  prepl. Documentation can be found in fif.impl.prepl/prepl-eval."
  fif.impl.prepl/prepl-eval)


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
      (stack.evaluators/eval-fn args)))


(defmacro eval
  "Evaluate the values provided in `body` within the fif
   stackmachine, and returns the stackmachine"
  [& body]
  `(eval-fn (quote ~body)))


(defn ieval-fn
  [sm args]
  (stack.evaluators/eval-fn sm args))


(defmacro ieval
  "Evaluate `body` forms within the provided stack-machine,
  `sm`. Returns the stack-machine after evaluation."
  [sm & body]
  `(ieval-fn ~sm (quote ~body)))


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
  "Safeily evaluates the given string as a stream of EDN values within
  the fif stackmachine, and returns the stackmachine after evaluation.
  "
  [s]
  (-> *default-stack* (stack.evaluators/eval-string s)))


(defn reval-string
  "Evaluates the given string as a stream of EDN values within the fif
  stackmachine, and returns the main stack in a more pleasing
  orientation."
  [s]
  (-> *default-stack* (stack.evaluators/eval-string s) stack/get-stack reverse))


#?(:clj 
   (defn eval-file
     "Evaluates the given file as a fif script. Returns the stack-machine
  after evaluation."
     [fpath]
     (let [fcontent (slurp fpath)]
       (-> *default-stack* (stack.evaluators/eval-string fcontent)))))


#?(:clj
   (defn reval-file
     "Evaluates the given file as a fif script. Returns the main stack in a
  more pleasing orientation."
     [fpath]
     (let [fcontent (slurp fpath)]
       (-> *default-stack* (stack.evaluators/eval-string fcontent) stack/get-stack reverse))))


#_(reval-file "examples/fif/templating.fif")


(defmacro dbg-eval
  "Debugging tool. By default enables the step inhibitor."
  [opts & body]
  `(let [step-max# (get ~opts :step-max 100)]
     (-> *default-stack*
         (stack/set-step-max step-max#)
         (stack.evaluators/eval-fn (quote ~body)))))


;;
;; Feature Ideas
;;

#_(reval 2 0 do {:id i} ?m loop) ;; => '({:id 0} {:id 1} {:id 2})

#_(reval (%:name %:age) {:name "Ben" :age 29} format) ;; (("Ben" 29))

#_(reval (%0 %1) ["Ben" 29] format) ;; (("Ben" 29))

#_(reval (% %) ["Ben" 29]) ;; ((["Ben" 29] ["Ben" 29]))

