(ns fif.stdlib.cond-loop-test
  (:require
   [clojure.test :refer :all]
   [fif.core :refer [reval dbg-eval]]
   [fif.stdlib.cond-loop-test :refer :all]))


(defmacro deval
  "Includes the ability to add a max-step to prevent infinite loops"
  [max-step & body]
  `(->
    (dbg-eval {:max-step ~max-step} ~@body)
    fif.core/get-stack
    reverse))


(deftest test-do-loop
   (is (= '(0 1 2 3 4)
          (deval 100 4 0 do i loop)))
   (is (= '() (deval 100 4 0 do leave 1 2 3 4 loop)))

   (is (= '(0 1) (deval 100 4 0 do i 2 = if leave then i loop)))

   (is (= '(0) (deval 100 0 0 do i loop))))


(deftest test-do-plus-loop
   (is (= '(0 2 4 6 8) (deval 200 8 0 do i 2 +loop))))
           

(deftest test-begin-until
   (is (= '(0 1 2 3 4 5) (deval 200 0 begin dup inc dup 5 = until)))

   (is (= '(0 2) (deval 200 0 begin dup inc inc leave 5 = until)))

   (is (= '(1 2 3) (deval 200 begin 1 begin 2 leave true until 3 leave false until))))


(deftest test-begin-while
   (is (= '(0 1 2 3 4 5) (deval 200 0 begin dup 5 < while dup inc repeat))))

