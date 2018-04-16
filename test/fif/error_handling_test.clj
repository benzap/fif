(ns fif.error-handling-test
  (:require
   [clojure.test :refer :all]
   [fif.stack :as stack]
   [fif-test.utils :refer [teval are-eq*]]
   [fif.error-handling :refer :all]))


(deftest test-error?
  (let [errobj (new-error-object "test")]
    (is (error-object? errobj))))


(deftest test-stack-error?
  (let [errobj (stack-error (stack/new-stack-machine) "test")]
    (is (stack-error-object? errobj))))


(deftest test-system-error?
  (let [errobj (system-error (stack/new-stack-machine) (ex-info "test" {}) "test")]
    (is (system-error-object? errobj))))
