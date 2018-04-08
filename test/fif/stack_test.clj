(ns fif.stack-test
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


