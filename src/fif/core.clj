(ns fif.core
  (:refer-clojure :exclude [eval])
  (:require [fif.stack :as stack]
            [fif.stdlib :refer [import-stdlib]]
            :reload-all))


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
   stackmachine, and returns the stackmachine"
  [& body]
  `(eval-fn (quote ~body)))


(defn ieval-fn
  [sm args]
  (stack/eval-fn sm args))


(defmacro ieval
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
  (-> *default-stack* (stack/eval-string s)))


(defn reval-string
  "Evaluates the given string as a stream of EDN values within the fif
  stackmachine, and returns the stack in a more pleasing orientation."
  [s]
  (-> *default-stack* (stack/eval-string s) stack/get-stack reverse))


(defmacro dbg-eval
  "Debugging tool. By default enables the step inhibitor"
  [opts & body]
  `(let [step-max# (get ~opts :step-max 100)]
     (-> *default-stack*
         (stack/set-step-max step-max#)
         (stack/eval-fn (quote ~body)))))

#_(reval
   vec! 4 1 do
     map!
       :id i pair
       :options 
       map!
         :animals set! :cat :dog :mouse ! pair
       ! pair
     !
     loop !)

#_(reval (1 2 3) apply) ;; (1 2 3)

#_(reval $ conj (1 2 3) 4 _) ;; ((4 1 2 3))

;; Stack Manipulation (- means leave, < means move)
;; so $-<< means leave first, move second, move third
#_(reval $>> a b c) ;; => ( b c a )

#_(reval $-<-<<

#_(reval $>> conj [1 2 3] 4 5) ;; ([1 2 3 4] 5)

#_(reval $> first [1 2 3] 5) ;; (1 5)

#_(reval $> a b c) ;; (b a c)

#_(reval $>> pair :id 1 2 3) ;; ([:id 1] 2 3)

#_(reval $>>> + 1 2 3 4) ;; (1 5 4)

#_(reval (1 1 +) apply) ;; (2)

#_(reval *x) ;; (x)

#_(reval **x) ;; (*x)
#_(reval ****x) ;; (***x)

#_(reval & * * + _) ;; (*+)

#_(reval &plus- 2) ;; (plus-2)

#_(reval &* + 1 1) ;; (+ 1 1)

#_(reval &** + 1 1) ;; (*+ 1 1)

#_(reval def x 10) ;; ()

#_(reval def x 10 x) ;; (10)

#_(reval def x 10 *x 20 set! x) ;; (20)

#_(reval *inc [1 2 3 4] map) ;; ((2 3 4 5))

#_(reval (1 +) [1 2 3 4] map) ;; ((2 3 4 5))

#_(reval *even? [1 2 3 4] filter) ;; ((2 4))

#_(reval (2 mod if true else false then) [1 2 3 4] filter) ;; ((2 4))

#_(reval *+ [1 2 3 4] 0 reduce) ;; (10)

#_(reval $>> map *inc [1 2 3 4])
