(ns fif.core-test
  (:refer-clojure :exclude [eval])
  (:require [clojure.test :refer :all]
            [fif.core :refer :all]
            [fif.stack :refer [new-stack-machine]]))


(deftest test-eval-fn
  (is (= '(2) (-> (eval-fn [1 1 '+]) get-stack))))


(deftest test-eval
  (is (= '(2) (-> (eval 1 1 +) get-stack))))


(deftest test-reval
  (is (= '(2) (-> (reval 1 1 + >r)))))


(deftest test-eval-string
  (is (= '(2) (-> (eval-string "1 1 +") get-stack))))


(deftest test-with-stack
  (is (= '(+ 1 1) (with-stack (new-stack-machine)
                    (-> (eval 1 1 +)
                        get-stack)))))
