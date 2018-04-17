(ns fif.core
  (:refer-clojure :exclude [eval])
  (:require
   [fif.stack :as stack]
   [fif.stack.evaluators :as stack.evaluators]
   [fif.stack.error-handling :refer [default-system-error-handler]]
   [fif.stdlib :refer [import-stdlib]]
   [fif.impl.stack :refer [new-stack-machine]]
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
  (-> (new-stack-machine)
      ;;(stack/set-system-error-handler default-system-error-handler)
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
  stackmachine, and returns the stack in a more pleasing orientation."
  [s]
  (-> *default-stack* (stack.evaluators/eval-string s) stack/get-stack reverse))


(defmacro dbg-eval
  "Debugging tool. By default enables the step inhibitor"
  [opts & body]
  `(let [step-max# (get ~opts :step-max 100)]
     (-> *default-stack*
         (stack/set-step-max step-max#)
         (stack.evaluators/eval-fn (quote ~body)))))

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

#_(reval
   [4 1 do
    {:id i :options {:animals #{:cat :dog :mouse}}} ?
    loop] ?)

#_(reval (1 2 3) apply) ;; (1 2 3)

#_(reval $ conj (1 2 3) 4 _) ;; ((4 1 2 3))

#_(reval $>> a b c) ;; => ( b c a )

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

#_(reval & constructing - value _)

#_(reval &plus- 2) ;; (plus-2)

#_(reval &* + 1 1) ;; (+ 1 1)

#_(reval &** + 1 1) ;; (*+ 1 1)

#_(reval def! x 10) ;; ()

#_(reval def! x 10 x) ;; (10)

#_(reval def! x 10 *x 20 set! x) ;; (20)

#_(reval *inc [1 2 3 4] map) ;; ((2 3 4 5))

#_(reval (1 +) [1 2 3 4] map) ;; ((2 3 4 5))

#_(reval *even? [1 2 3 4] filter) ;; ((2 4))

#_(reval (2 mod if true else false then) [1 2 3 4] filter) ;; ((2 4))

#_(reval *+ [1 2 3 4] 0 reduce) ;; (10)

#_(reval $>> map *inc [1 2 3 4])

#_(reval $>> map (1 +) [1 2 3 4]) ;; ([2 3 4 5])

#_(reval 2 $-< 3 1 4) ;; '(1 2 3 4)
#_(reval 2 $< 1 3 4) ;; '(1 2 3 4)
#_(reval 3 $-<< 1 2 4 5) ;; '(1 2 3 4 5)

#_(reval
   *even? *inc [0 4 do i loop] ? map filter) ;; ([2 4])

#_(reval
   [4 0 do i loop] ?
   $-< map *inc
   $-< filter *even?) ;; ([2 4])

#_(reval [4 0 do i loop] ?) ;; => '([0 1 2 3 4])

#_(reval [2 0 do [i 0] ? loop] ?) ;; => '([[0 0] [0 1] [0 2]])

#_(reval 2 0 do {:id i} ? loop) ;; => '({:id 0} {:id 1} {:id 2})

#_(reval
   $>> range 0 5
   $-< map *inc
   $-< filter *even?
   apply) ;; (2 4)

#_(->> (range 0 5)
       (map inc)
       (filter even?))

#_(reval println 1 2)

#_(reval 1 {} conj)
