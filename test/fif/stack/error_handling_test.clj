(ns fif.stack.error-handling-test
  (:require
   [clojure.test :refer :all]
   [fif.stack :as stack]
   [fif.impl.stack :refer [new-stack-machine]]
   [fif-test.utils :refer [teval are-eq*]]
   [fif.stack.error-handling :refer :all]))


(deftest test-error?
  (let [errobj (new-error-object "test")]
    (is (error-object? errobj))))


(deftest test-stack-error?
  (let [errobj (stack-error (new-stack-machine) "test")]
    (is (stack-error-object? errobj))))


(deftest test-system-error?
  (let [errobj (system-error (new-stack-machine) (ex-info "test" {}) "test")]
    (is (system-error-object? errobj))))
