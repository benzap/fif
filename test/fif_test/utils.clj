(ns fif-test.utils
  (:require 
   [clojure.test :refer :all]
   [fif.core :as fif]
   [fif.stack :as stack]))


(defmacro test-eval
  [{:keys [step-max] :or {step-max 9001}} body]
  `(let [test-sm# (-> fif/*default-stack*
                      (stack/set-step-max ~step-max))
         sm-result# (fif/with-stack test-sm#
                      (-> (fif/eval-fn (quote ~body))
                          stack/get-stack
                          reverse))]
     sm-result#))


#_(test-eval {} (2 2 +))


(defmacro are-test-eval [])


#_(are-test-eval 
   (2 2 +) => (4)
   (1 1 -) => (1))
