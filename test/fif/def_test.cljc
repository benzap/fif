(ns fif.def-test
  (:refer-clojure :exclude [eval])
  (:require
    [clojure.test :refer [deftest testing is are]]
    [fif.stack-machine :as stack]
    [fif.impl.stack-machine :refer [new-stack-machine]]
    [fif.def :refer [wrap-function-with-arity
                     wrap-procedure-with-arity
                     defcode-eval]
     :include-macros true]
    [fif.core :as fif :refer [with-stack] :include-macros true]
    [fif-test.utils :refer [teval are-eq*]]))


(def test-stack-machine (new-stack-machine))


(deftest test-wrap-function-with-arity
   (let [add2 (wrap-function-with-arity 1 #(+ 2 %))
         sm (-> test-stack-machine
                (stack/set-word 'add2 add2))]
     (is (= '(3) (with-stack sm (teval 1 add2))))))


(def ^:dynamic *test-val* nil)
(deftest test-wrap-procedure-with-arity
  (binding [*test-val* (atom 1)]
    (let [update-val! (fn [i] (reset! *test-val* i))
          op-update-val! (wrap-procedure-with-arity 1 update-val!)
          sm (-> test-stack-machine
                 (stack/set-word 'update-val! op-update-val!))
          _ (with-stack sm (teval 2 update-val!))]
      (is (= 2 @*test-val*)))))


(defcode-eval import-add2-library
  fn add2 2 + endfn)


(deftest test-wrap-code-eval
  (let [custom-stack-machine (-> fif/*default-stack* import-add2-library)]
    (with-stack custom-stack-machine
      (is (= '(4) (teval 2 add2))))))
