(ns fif.stack-machine-test
  (:require [clojure.test :refer [deftest testing is are]]
            [fif.stack-machine :refer [push-stack
                                       get-stack
                                       pop-stack
                                       push-ret
                                       get-ret
                                       clear-stack
                                       set-stack
                                       pop-ret
                                       push-flag
                                       pop-flag
                                       enqueue-code
                                       dequeue-code
                                       get-code
                                       set-code
                                       step
                                       run]]
            [fif.stack-machine.evaluators :as evaluators]
            [fif.stack-machine.flags :as stack.flags]
            [fif.stack-machine.words :as stack.words]
            [fif.impl.stack-machine :refer [new-stack-machine]]))


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


#_(deftest test-stackmachine-words
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
      (is (stack.flags/has-flags? (-> s (push-flag :test)))))))


(deftest test-process-mode)


(deftest test-process-arg)


(deftest test-eval-fn
  (let [s (-> (new-stack-machine)
              (push-stack 1)
              (enqueue-code 2)
              (enqueue-code 3)
              (evaluators/eval-fn ['val]))]
    (is (= (get-stack s) '(val 3 2 1)))))


(deftest test-eval
  (let [s (-> (new-stack-machine)
              (push-stack 1)
              (enqueue-code 2)
              (evaluators/eval val))]
    (is (= (get-stack s) '(val 2 1)))))


(deftest test-wrap-eval-string
  (is (= "\n[\ntest\n]\n" (evaluators/wrap-eval-string "test"))))


(deftest test-eval-string
  (let [s (-> (new-stack-machine)
              (push-stack 1)
              (enqueue-code 2)
              (evaluators/eval-string "3 val"))]
    (is (= (get-stack s) '(val 3 2 1)))))
