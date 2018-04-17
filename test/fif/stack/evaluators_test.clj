(ns fif.stack.evaluators-test
  (:refer-clojure :exclude [eval])
  (:require
   [clojure.test :refer :all]
   [fif.stack :as stack]
   [fif.stack.evaluators :refer :all]
   [fif-test.utils :refer [are-eq* teval]]))
