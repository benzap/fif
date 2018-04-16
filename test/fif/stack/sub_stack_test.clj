(ns fif.stack.sub-stack-test
  (:require
   [clojure.test :refer :all]
   [fif.stack.sub-stack :refer :all]))


(deftest test-create-sub-stack
  (is (= '() (-> (create-sub-stack '()) peek))))


(deftest test-push-sub-stack
  (is (= '(1) (-> '() create-sub-stack (push-sub-stack 1) peek))))


(deftest test-pop-sub-stack
  (is (= '(1) (-> '()
                  create-sub-stack
                  (push-sub-stack 1)
                  (push-sub-stack 2)
                  (push-sub-stack 3)
                  pop-sub-stack
                  pop-sub-stack
                  peek))))


(deftest test-get-sub-stack
  (is (= '(3 2 1) (-> '()
                      create-sub-stack
                      (push-sub-stack 1)
                      (push-sub-stack 2)
                      (push-sub-stack 3)
                      get-sub-stack))))


(deftest test-set-sub-stack
  (is (= '(1 2) (-> '()
                    create-sub-stack
                    (push-sub-stack 1)
                    (push-sub-stack 2)
                    (push-sub-stack 3)
                    (set-sub-stack '(1 2))
                    get-sub-stack))))


(deftest test-remove-sub-stack
  (is (= '() (-> '()
                 create-sub-stack
                 (push-sub-stack 1)
                 (push-sub-stack 2)
                 (push-sub-stack 3)
                 (set-sub-stack '(1 2))
                 remove-sub-stack))))
