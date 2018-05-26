(ns fif.def
  (:require
   [clojure.string :as str]
   [fif.stack-machine :as stack]
   [fif.stack-machine.evaluators :as evaluators]
   [fif.stack-machine.error-handling :as error-handling]
   [fif.stack-machine.exceptions :as exceptions]
   [fif.stack-machine.verification :as verification]
   [fif.stack-machine.words :as words]
   [fif.stack-machine.variable :as variable]))


(defn wrap-code-eval
  [args]
  (fn [sm]
    (-> sm
        (evaluators/eval-fn args)
        (stack/set-step-num 0))))


(defn wrap-function-with-arity
  "Wraps a clojure function `f` which accepts `num-args`, and returns
  the function wrapped to be used in a stack machine.

  The returned function accepts `num-args` values on the stack, drops
  `num-args` after processing the wrapped function, and pushes the
  result of `(apply f args)` back onto the stack.
  
  Notes:

  This wrapper always returns a result on the stack. If you do not
  wish to return a result, use `fif.def/wrap-procedure-with-arity`
  instead.

  Examples:

  (defn add2 [x] (+ x 2))
  (def my-stack-machine (-> (fif.stack/new-stack-machine)
                            (fif.stack/set-word 'add2 (fif.def/wrap-function-with-arity 1 add2))))
  (fif.core/with-stack my-stack-machine
     (fif.core/reval 1 add2)) ;; => '(3)"
  [num-args f]
  (fn [sm]
    (cond
      ;; Check to see if the main stack has enough arguments to
      ;; satisfy the word operation.
      (not (verification/stack-satisfies-arity? sm num-args))
      (exceptions/raise-incorrect-arity-error sm num-args)

      :else
      (let [args (take num-args (stack/get-stack sm))
            result (apply f (reverse args))
            new-stack (as-> sm $
                        (stack/get-stack $)
                        (drop num-args $)
                        (concat [result] $)
                        (apply list $))]
        (-> sm
            (stack/set-stack new-stack)
            stack/dequeue-code)))))


(defn wrap-procedure-with-arity
  "Wraps a clojure function `f` which accepts `num-args`, and returns
  the function wrapped to be used in a stack machine.

  The returned function accepts `num-args` values on the stack, drops
  `num-args` after processing the wrapped function.
  
  Notes:

  This wrapper never returns a result on the stack. If you wish to
  return a result, use `fif.def/wrap-function-with-arity` instead.

  Examples:

  (def val (atom nil))
  (defn set-val! [x] (reset! val x))
  (def my-stack-machine (-> (fif.stack/new-stack-machine)
                            (fif.stack/set-word 'set-val!
                                                (fif.def/wrap-procedure-with-arity 1 set-val!))))
  (fif.core/with-stack my-stack-machine
     (fif.core/reval 1 set-val!)
     (deref val)) ;; => 1
  "
  [num-args f]
  (fn [sm]
    (cond
      ;; Check to see if the main stack has enough arguments to
      ;; satisfy the word function.
      (not (verification/stack-satisfies-arity? sm num-args))
      (exceptions/raise-incorrect-arity-error sm num-args)

      :else
      (let [args (take num-args (stack/get-stack sm))
            _ (apply f (reverse args))
            new-stack (->> sm stack/get-stack (drop num-args))]
        (-> sm
            (stack/set-stack (into '() new-stack))
            stack/dequeue-code)))))


(defmacro defcode-eval
  "Allows you to define functions that contain fif code, which can then
  be passed through a fif stack machine to be evaluated.

  Example:

  (defcode-eval import-add2-library
    fn add2
      + 2
    endfn)

  (def custom-stack-machine
    (-> fif.core/*default-stack*
        import-add2-library))

  (fif.core/with-stack custom-stack-machine
    (fif.core/reval 2 add2)) ;; => '(4)"
  [name & body]
  `(def ~name (wrap-code-eval (quote ~body))))


;;
;; Define Stack Functions
;;


(defmacro defstack-func-0 [name f]
  `(def ~name (wrap-function-with-arity 0 ~f)))


(defmacro defstack-func-1 [name f]
  `(def ~name (wrap-function-with-arity 1 ~f)))


(defmacro defstack-func-2 [name f]
  `(def ~name (wrap-function-with-arity 2 ~f)))


(defmacro defstack-func-3 [name f]
  `(def ~name (wrap-function-with-arity 3 ~f)))


;;
;; Define Stack Procedures
;;


(defmacro defstack-proc-0 [name f]
  `(def ~name (wrap-procedure-with-arity 0 ~f)))


(defmacro defstack-proc-1 [name f]
  `(def ~name (wrap-procedure-with-arity 1 ~f)))


(defmacro defstack-proc-2 [name f]
  `(def ~name (wrap-procedure-with-arity 2 ~f)))


(defmacro defstack-proc-3 [name f]
  `(def ~name (wrap-procedure-with-arity 3 ~f)))


;;
;; Define Global Variables
;;


(def wrap-variable variable/wrap-global-variable)


(defn set-word-variable
  "Creates a new word variable in `sm` with the symbol name `wname` and
  with the value `value`. Optionally, you can include a docstring, and
  a group key.

  Notes:

  - `value` is automatically wrapped for you. This is not the case
  with `set-word-definition`."
  [sm wname value & {:keys [doc group]}]
  (words/set-global-word-defn
   sm
   wname (wrap-variable value)
   :doc doc :group group
   :stdlib? false
   :variable? true))


;;
;; Define Global Words
;;


(defn set-word-function
  "Creates a new word function in `sm` with the symbol name `wname` and
  with the word function defined by `wfunc`.

  Notes:

  - `wfunc` is a stack-machine function. Normal clojure functions can
  be turned into stack-machine functions via
  `wrap-function-with-arity` and `wrap-procedure-with-arity`."
  [sm wname wfunc & {:keys [doc group]}]
  (words/set-global-word-defn
   sm
   wname wfunc
   :doc doc :group group
   :stdlib? false
   :variable? false))
