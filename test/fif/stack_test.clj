(ns fif.stack-test
  (:refer-clojure :exclude [eval])
  (:require [clojure.test :refer :all]
            [fif.stack :refer :all]))


(deftest test-stackmachine-stacks
  (testing "pushing and popping on the arg stack"
    (let [s (-> (new-stack-machine)
                (push-stack 1)
                (push-stack 2)
                (push-stack 3))]
      (is (= '(3 2 1) (get-stack s)))
      (is (= '(2 1) (-> s pop-stack get-stack)))
      (is (= '(1 2 3) (-> s (set-stack '(1 2 3)) (get-stack))))
      (is (= '() (-> s clear-stack get-stack)))))
 
  (testing "pushing and popping on the return stack"
    (let [s (-> (new-stack-machine)
                (push-stack 1)
                (push-stack 2)
                (push-stack 3))]
      (is (= '(1) (-> s (push-ret 1) get-ret)))
      (is (= '(1) (-> s (push-ret 1) (push-ret 2) pop-ret get-ret)))))

  (testing "adding and removing words"))


(defn wrap-cword [cval]
  (fn [sm]
    (-> sm (push-stack cval))))


(deftest test-stackmachine-words
  (testing "setting and removing words"
    (let [s (-> (new-stack-machine)
                (push-stack 1)
                (push-stack 2)
                (push-stack 3))]
      (is (get (-> s (set-word 'val (wrap-cword nil))
                     (get-words))
               'val))

      (let [s (-> s (set-word 'val (wrap-cword nil))
                    (remove-word 'val))]
        (is (nil? (get (-> s get-words) 'val)))))))


(deftest test-stackmachine-run
  (testing "Test stepping through stackmachine"
    (let [s (-> (new-stack-machine)
                (push-stack 1)
                (push-stack 2)
                (push-stack 3)
                (enqueue-code 4))
          s2 (step s)]
      (is (= '(4 3 2 1) (-> s2 get-stack))))
      
    (let [s (-> (new-stack-machine)
                (push-stack 1)
                (push-stack 2)
                (push-stack 3)
                (enqueue-code 4)
                (enqueue-code 5))
          s2 (run s)]
      (is (= '(5 4 3 2 1) (-> s2 get-stack))))))


(deftest test-has-flags
  (testing "Main Test"
    (let [s (-> (new-stack-machine)
                (push-stack 1)
                (push-stack 2))]
      (is (has-flags? (-> s (push-flag :test)))))))


(deftest test-process-mode)


(deftest test-process-arg)


(deftest test-eval-fn
  (let [s (-> (new-stack-machine)
              (push-stack 1)
              (enqueue-code 2)
              (enqueue-code 3)
              (eval-fn ['val]))]
    (is (= (get-stack s) '(val 3 2 1)))))


(deftest test-eval
  (let [s (-> (new-stack-machine)
              (push-stack 1)
              (enqueue-code 2)
              (eval val))]
    (is (= (get-stack s) '(val 2 1)))))


(deftest test-wrap-eval-string
  (is (= "[test]" (wrap-eval-string "test"))))


(deftest test-eval-string
  (let [s (-> (new-stack-machine)
              (push-stack 1)
              (enqueue-code 2)
              (eval-string "3 val"))]
    (is (= (get-stack s) '(val 3 2 1)))))


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
