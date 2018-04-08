(ns fif.core-test
  (:require [clojure.test :refer :all]
            [fif.core :refer :all]
            [fif.stack :refer [new-stack-machine]]))


(deftest test-fif-fn
  (is (= '(2) (-> (fif-fn [1 1 '+]) get-stack))))


(deftest test-fif-eval
  (is (= '(2) (-> (fif-eval 1 1 +) get-stack))))


(deftest test-fif-reval
  (is (= '(2) (-> (fif-reval 1 1 + >r)))))


(deftest test-fif-eval-string
  (is (= '(2) (-> (fif-eval-string "1 1 +") get-stack))))


(deftest test-with-stack
  (is (= '(+ 1 1) (with-stack (new-stack-machine)
                    (-> (fif-eval 1 1 +)
                        get-stack)))))
