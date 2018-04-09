(ns fif.core
  (:refer-clojure :exclude [eval])
  (:require [fif.stack :as stack]
            [fif.stdlib :refer [import-stdlib]]))


(def get-stack stack/get-stack)
(def get-ret stack/get-ret)


(def ^:dynamic *default-stack*
  (-> (stack/new-stack-machine)
      import-stdlib))


(defmacro with-stack [sm & body]
  `(binding [*default-stack* ~sm]
     ~@body))


(defn eval-fn [args]
  (-> *default-stack*
      (stack/eval-fn args)))


(defmacro eval [& body]
  `(eval-fn (quote ~body)))


(defmacro reval [& body]
  `(-> (eval-fn (quote ~body)) stack/get-ret))


(defn eval-string [s]
  (-> *default-stack* (stack/eval-string s)))




#_(reval 1 1 + dup >r 1 + >r)


#_(reval

   1 1 + . cr ;; First Example
   1 1 + 1 - . cr
   fn addtwo
   2 +
   endfn
   
   2 addtwo . cr
   
   2 addtwo addtwo . cr
   
   fn addfour
   addtwo addtwo
   endfn
   
   4 addfour . cr
   
   2 2 - if 1 1 + else 2 2 + then . cr
   
   fn cond1
   if true else false then
   endfn
   
   fn cond2
   if 1 else 2 then
   endfn
   
   2 2 - cond1 cond2 . cr
   
   variable x
   100 x !
   x at . cr
   
   variable y
   {:x 123} y !
   y at . cr

   9000 constant VAL

   VAL dup dup . cr

   .s)


#_(reval
   fn factorial
   dup 1 > if dup dec factorial * then
   endfn
   
   5 factorial
   >r)


#_(reval fn add2 2 + endfn 2 add2 >r)


#_(eval
   fn cond1 if true else false then endfn

   0 cond1 .s)


#_(reval 1 1 + dup >r 1 + >r)

#_(reval 2 2 - if 1 1 + else 2 2 + then >r)

#_(reval 1 1 = >r)

#_(reval true if 1 else 2 then >r)


#_(eval fn add2 2 + endfn 2 add2)


#_(reval
   12 dup 18 <  if "You are underage"      else
   dup 50 <  if "You are the right age" else
   dup 50 >= if "You are too old"       else
   then then then >r)

#_(reval
   fn check-age
     dup 18 <  if "You are underage"      else
     dup 50 <  if "You are the right age" else
     dup 50 >= if "You are too old"       else
     then then then
   endfn

   12 check-age >r
   24 check-age >r
   51 check-age >r)

#_(reval

   1
   if
    2 2 +
   else
    2 2 -
   then

   >r)

#_(reval 23 dup 18 < >r)


#_(reval 2 dup dup 3 4)

#_(eval
   fn addtwo
   2 +
   endfn

   2 addtwo .s)
