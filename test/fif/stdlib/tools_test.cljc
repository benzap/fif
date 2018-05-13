(ns fif.stdlib.tools-test
  (:require
   [clojure.test :refer [deftest testing is are]]
   [fif.stdlib.tools]
   [fif.core :refer [reval]]
   [fif-test.utils :refer [teval are-eq*]]))


(deftest test-reset-stack-machine
  (are-eq*
   (teval if $reset-stack-machine 1 1 +)
   
   => '(2)))
