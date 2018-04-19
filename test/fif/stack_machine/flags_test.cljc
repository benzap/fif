(ns fif.stack-machine.flags-test
  (:require
   [clojure.test :refer [deftest testing is are]]
   [fif.stack-machine :as stack]
   [fif.stack-machine.flags :as stack.flags]
   [fif-test.utils :refer [are-eq* teval]]))
