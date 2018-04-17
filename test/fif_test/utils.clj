(ns fif-test.utils
  (:require 
   [clojure.test :refer :all]
   [fif.core :as fif :refer [reval]]
   [fif.stack :as stack]))


(def ^:dynamic *test-step-max* 10000)
(def max-step-exceeded-keyword ::max-step-exceeded)

(defn teval-fn
  ([args]
   (let [test-sm (-> fif/*default-stack*
                     (stack/set-step-max *test-step-max*))
         sm-result (fif/with-stack test-sm (fif/eval-fn args))
         step-num (stack/get-step-num sm-result)]
     (if-not (>= step-num *test-step-max*)
       (-> sm-result stack/get-stack reverse)
       max-step-exceeded-keyword))))


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
