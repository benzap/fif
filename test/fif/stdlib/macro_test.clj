(ns fif.stdlib.macro-test
  (:require
   [clojure.test :refer :all]
   [fif.core :refer [reval]]
   [fif.stdlib.macro :refer :all]))


(deftest test-macro-creation
   (is (= '("start" true 3 1 "end")
          (reval
           macro some_value
           true
           if 
             nil drop $-| 3 1 |-$ true
           else
             nil drop $-| 2 1 |-$ false
           then
           endmacro
           "start" some_value "end"))))
