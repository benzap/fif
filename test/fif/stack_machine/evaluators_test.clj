(ns fif.stack-machine.evaluators-test
  (:refer-clojure :exclude [eval])
  (:require
   [clojure.test :refer :all]
   [fif.stack-machine :as stack]
   [fif.stack-machine.evaluators :refer :all]
   [fif-test.utils :refer [are-eq* teval]]))
