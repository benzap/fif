(ns fif.stack-machine.error-handling-test
  (:require
   [clojure.test :refer [deftest testing is are]]
   [fif.stack-machine :as stack]
   [fif.impl.stack-machine :refer [new-stack-machine]]
   [fif-test.utils :refer [teval are-eq*]]
   [fif.stack-machine.error-handling :refer [error-object?
                                             new-error-object
                                             stack-error
                                             stack-error-object?
                                             system-error
                                             system-error-object?]]))


(deftest test-error?
  (let [errobj (new-error-object "test")]
    (is (error-object? errobj))))


(deftest test-stack-error?
  (let [errobj (stack-error (new-stack-machine) "test")]
    (is (stack-error-object? errobj))))


(deftest test-system-error?
  (let [errobj (system-error (new-stack-machine) (ex-info "test" {}) "test")]
    (is (system-error-object? errobj))))
