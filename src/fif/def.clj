(ns fif.def
  (:refer-clojure :exclude [eval])
  (:require
   [clojure.string :as str]
   [fif.stack :refer :all]))


(defn wrap-code-eval
  [args]
  (fn [sm]
    (-> sm 
        (eval-fn args)
        (set-step-num 0))))


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
     (let [args (take num-args (get-stack sm))
           ;; TODO: raise error if (count args) < num-args
           result (apply f (reverse args))
           new-stack (-> sm
                         get-stack
                         (as-> $ (drop num-args $))
                         (conj result)
                         (as-> $ (into '() $))
                         reverse)]
       (-> sm
           (set-stack new-stack)
           dequeue-code))))


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
     @val) ;; => 1
  "
  [num-args f]
  (fn [sm]
     (let [args (take num-args (get-stack sm))
           ;;TODO: raise error if (count args) < num-args
           _ (apply f (reverse args))
           new-stack (->> sm get-stack (drop num-args))]
       (-> sm
           (set-stack (into '() new-stack))
           dequeue-code))))

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



