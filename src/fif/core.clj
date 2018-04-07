(ns fif.core
  (:require [fif.stack :as stack]
            [fif.stdlib :refer [import-stdlib]]
            [fif.compile :refer [import-compile-mode]]))


(def get-stack stack/get-stack)
(def get-ret stack/get-ret)


(def ^:dynamic *default-stack*
  (-> (stack/new-stack-machine)
      import-stdlib
      import-compile-mode))


(defn fif-fn [args]
  (-> *default-stack*
      (stack/eval-fn args)))


#_(fif-fn [1 1 '+])


(defmacro fif-eval [& body]
  `(fif-fn (quote ~body)))


#_(fif-eval 1 1 + >r)
#_(fif-eval variable x 100 x ! x at)


(defmacro fif-reval [& body]
  `(-> (fif-fn (quote ~body)) stack/get-ret))


#_(fif-reval 1 1 + dup >r 1 + >r)


(-> (stack/new-stack-machine)
    (import-stdlib)
    (import-compile-mode)

    (stack/eval

     1 1 + . ;; First Example
     1 1 + 1 - .
     fn addtwo
       2 +
     endfn

     2 addtwo .

     2 addtwo addtwo .

     fn addfour
       addtwo addtwo
     endfn

     4 addfour .

     2 2 - if 1 else 2 then .

     fn cond1
       if true else false then
     endfn

     fn cond2
       if 1 else 2 then
     endfn

     2 2 - cond1 cond2 .

     variable x
     100 x !
     x at .

     .s))
