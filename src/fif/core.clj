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
#_(fif-reval variable x 100 x ! x at >r)


(defmacro fif-reval [& body]
  `(-> (fif-fn (quote ~body)) stack/get-ret))


#_(fif-reval 1 1 + dup >r 1 + >r)


#_(fif-reval

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


#_(fif-reval
   fn factorial
   dup 1 > if dup dec factorial * else then
   endfn
   
   5 factorial
   >r)


#_(fif-eval
   fn cond1 if true else false then endfn

   0 cond1 .s)


#_(fif-reval 1 1 + dup >r 1 + >r)

#_(fif-reval 2 2 - if 1 1 + else 2 2 + then >r)

#_(fif-reval 1 1 = >r)

#_(fif-reval true if 1 else 2 then >r)


#_(fif-eval fn add2 2 + endfn 2 add2)


#_(fif-reval
   12 dup 18 <  if "You are underage"      else
   dup 50 <  if "You are the right age" else
   dup 50 >= if "You are too old"       else
   then then then >r)

#_(fif-reval
   fn check-age
     dup 18 <  if "You are underage"      else
     dup 50 <  if "You are the right age" else
     dup 50 >= if "You are too old"       else
     then then then
   endfn

   12 check-age >r
   24 check-age >r
   51 check-age >r)

#_(fif-reval

   1
   if
    2 2 +
   else
    2 2 -
   then

   >r)

#_(fif-reval 23 dup 18 < >r)


#_(fif-eval 2 dup dup 3 4)

#_(fif-eval
   fn addtwo
   2 +
   endfn

   2 addtwo .s)
