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


(defmacro seval [& body]
  `(-> (eval-fn (quote ~body)) stack/get-stack))


(defmacro reval [& body]
  `(-> (eval-fn (quote ~body)) stack/get-stack reverse))


(defn eval-string [s]
  (-> *default-stack* (stack/eval-string s)))


(defmacro dbg-eval [opts & body]
  `(let [step-max# (get ~opts :step-max 100)]
     (-> *default-stack*
         (stack/set-step-max step-max#)
         (stack/eval-fn (quote ~body)))))


#_(->> (dbg-eval {:step-max 500} 10 1 do 5 1 do j i 2 +loop 1 1 + +loop)
      stack/get-stack
      reverse
      (partition 2))

#_(->> (dbg-eval {:step-max 200} 1 2 do 1 2 do j i loop loop)
       (stack/get-stack))

#_(->> (dbg-eval {:step-max 200} 1 2 do i 1 +loop)
       (stack/get-stack))

#_(->> (dbg-eval {:step-max 20} begin begin 3 1 until 2 1 until 2)
       (stack/get-stack))

#_(->> (dbg-eval {:step-max 103}
        begin 1 while
          begin true if 1 then while 4 repeat 5
        repeat
        3)
       stack/get-stack)


#_(->> (dbg-eval {:step-max 500} 10 1 do leave 5 1 do j i 2 +loop 1 1 + +loop)
      stack/get-stack
      reverse
      (partition 2))

#_(->> (dbg-eval {:step-max 500} 10 1 do leave 5 1 do j i 2 +loop 1 1 + +loop)
      stack/get-stack)

#_(->> (dbg-eval {:step-max 60}

         macro some_value
           true
           if
             _$ 3 1 do $_
           else
             _$ 2 1 do $_
           then
         endmacro
         some_value 1 loop "end")
   stack/get-stack)


#_(reval

   def I 0
   
   fn incv
   dup getv inc swap setv
   endfn
   
   begin 1 while
   I incv
   I getv 10 > if leave then
   I getv
   repeat) 


#_(reval def I 1
         fn incv
           dup getv inc swap setv
         endfn

         I incv
         I getv
         I incv
         I getv)



#_(reval
   fn hello
     "Hello " . . "!" . cr
   endfn

   "Ben" hello)

#_(reval 2 2 +)



#_(reval
   macro some_value
   true
   if
   _$ 1 $_
   else
   _$ 2 $_
   then
   endmacro
   some_value)
   


#_(reval
   macro ?do
     over over >
     if
       _$ do $_
     else
       _$ do leave $_
     then
   endmacro

   fn yeaa!
     #_"(n -- ) Prints yeaa with 'n' a's"
     "yeeee" .
     0 ?do "a" . loop
     "hhh!" . cr
   endfn

   0 yeaa!)


#_(->> (dbg-eval {:step-max 50}

        true if false if 1 then else true if 1 else 2 then then)
       stack/get-stack)


#_(seval
   3 1 do i loop

   fn square dup * endfn
   fn squares 0 do i square loop endfn

   10 squares
   #=(+ 1 1))


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
   
   def x 100
   100 x !
   x at . cr
   
   def y {:x 123}
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
   19 dup  18  <  if drop "You are underage"      else
      dup 50 <  if drop "You are the right age"   else
      dup 50 >= if drop "You are too old"         else
      then then then)

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
