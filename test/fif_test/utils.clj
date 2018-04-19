(ns fif-test.utils
  (:require 
   [clojure.test :refer :all]
   [fif.core :as fif :refer [reval]]
   [fif.stack-machine :as stack]))


(def ^:dynamic *test-step-max* 10000)
(def max-step-exceeded-keyword ::max-step-exceeded)


(defn teval-fn
  "Used to test fif evaluation, while ensuring the stackmachine is in
  good health after evaluation.

  Notes:

  - teval assumes full evaluation back to the global environment
  scope, otherwise it will error out with an appropriate error
  message."
  ([args]
   (let [test-sm (-> fif/*default-stack*
                     (stack/set-step-max *test-step-max*))
         sm-result (fif/with-stack test-sm (fif/eval-fn args))
         step-num (stack/get-step-num sm-result)]
     (cond
       
       ;; Infinite Loop Protection. Ensure that there is no infinite loop
       (>= step-num *test-step-max*)
       max-step-exceeded-keyword
       
       ;; A healthy stack machine has no flags active.
       (not (empty? (stack/get-flags sm-result)))
       ::unmanaged-flags

       ;; There are no return values on the return stack when the
       ;; stack machine is healthy.
       (not (empty? (stack/get-ret sm-result)))
       ::return-stack-populated

       ;; The global scope is all that is left when the stack machine
       ;; is healthy.
       (not (= 1 (count (stack/get-scope sm-result))))
       ::scope-out-of-bounds

       :else
       (-> sm-result stack/get-stack reverse)))))


(deftest test-teval-fn
  (is (= '(4) (teval-fn '(2 2 +)))))


(defmacro teval [& body]
  `(teval-fn (quote ~body)))


(deftest test-teval
  (is (= '(4) (teval 2 2 +))))


(defmacro are-eq* [& body]
  `(are [x# _sep# y#] (= y# x#)
     ~@body))


(deftest test-are-eq*
  (testing "Simple Addition"
    (are-eq*
     (teval 2 2 +) => '(4))))
