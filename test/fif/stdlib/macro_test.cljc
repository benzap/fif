(ns fif.stdlib.macro-test
  (:require
   [clojure.test :refer [deftest testing is are]]
   [fif.stdlib.macro]
   [fif-test.utils :refer [teval are-eq*]]))


(deftest test-macro-creation
   (is (= '("start" true 3 1 "end")
          (teval
           macro some_value
           true
           if 
             nil drop _! 3 1 !_ true
           else
             nil drop _! 2 1 !_ false
           then
           endmacro
           "start" some_value "end"))))
