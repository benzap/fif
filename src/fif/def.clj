(ns fif.def
  (:refer-clojure :exclude [eval])
  (:require
   [clojure.string :as str]
   [fif.stack :refer :all]))


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

  (defn add2 [x] (+ x 2)
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



