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

     2 2 - if 1 1 + else 2 2 + then .

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

     9000 constant VAL

     VAL .

     .s))

#_(fif-reval 1 1 + dup >r 1 + >r)

(fif-reval 2 2 - if 1 1 + else 2 2 + then >r)

(fif-reval 1 1 = >r)

(fif-reval true if 1 else 2 then >r)

(fif-eval
  23 dup 18 <  if "You are underage"      else
     dup 50 <  if "You are the right age" else
     dup 50 >= if "You are too old"       else
     then then then >r)

(fif-eval false
  if
    2 2 +
  else
    dup if true else false then
  then

  >r)

(fif-reval 23 dup 18 < >r)


(fif-eval 2 dup dup 3 4)
