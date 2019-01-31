(ns fif.stdlib.math-test
  (:require
   [clojure.test :refer [deftest testing is are] :include-macros true]
   [fif.stdlib.math]
   [fif.stack-machine :as stack]
   [fif.core :as fif]
   [fif-test.utils :refer [teval are-eq*] :include-macros true]))


(deftest test-PI-op
  (are-eq*
   (teval PI)

   => #?(:clj (list Math/PI) :cljs (list (.-PI js/Math)))))
