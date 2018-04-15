(ns fif.token-test
  (:require
   [clojure.test :refer :all]
   [fif.token :refer :all]
   [fif.stack :as stack]))
    

(deftest test-take-to-token
  (is (= '() (take-to-token '() 'test)))
  (is (= '(1 2) (take-to-token '(1 2 test 3 4) 'test)))
  (is (= '(1 2 test2 3 4) (take-to-token '(1 2 test2 3 4) 'test))))


(deftest test-strip-token
  (is (= '(1 2) (strip-token '(if 1 2) 'if)))
  (is (= '(1 if 2) (strip-token '(if 1 if 2) 'if)))
  (is (= '(1 2) (strip-token '(if 1 2 if) 'if))))


(deftest test-rest-at-token
  (is (= '(1 2) (rest-at-token '(if 1 2) 'if)))
  (is (= '() (rest-at-token '(if 1 2 then) 'else))))


(deftest test-between-tokens
  (is (= '(1 1 +) (between-tokens '(if 1 1 + else 2 2 + then) 'if 'else)))
  (is (= '(1 1 + else 2 2 +) (between-tokens '(if 1 1 + else 2 2 + then) 'if 'then))))


(deftest test-split-at-token
  (is (= ['(1) '(2)] (split-at-token '(1 else 2) 'else)))
  (is (= ['(1 2) '(3 4)] (split-at-token '(1 2 else 3 4) 'else)))
  (is (= ['(1 2) '()] (split-at-token '(1 2) 'else)))
  (is (= ['() '(3 4)] (split-at-token '(else 3 4) 'else))))


(deftest test-replace-token
  (is (= '(this 1 2) (replace-token '(that 1 2) 'that 'this))))


(deftest test-push-coll
  (is (= '(4 3 2 1) (push-coll '(2 1) '(4 3)))))
