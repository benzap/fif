(ns fif.stack-machine.evaluators-test
  (:require
   [clojure.test :refer [deftest testing is are]]
   [fif.stack-machine :as stack]
   [fif.stack-machine.evaluators :as stack.evaluators]
   [fif-test.utils :refer [are-eq* teval]]))
