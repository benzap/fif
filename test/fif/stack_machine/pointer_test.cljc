(ns fif.stack-machine.pointer-test
  (:require
   [clojure.test :refer [deftest testing is are]]
   [fif.stack-machine :as stack]
   [fif.stack-machine.pointer :refer [arg-is-pointer?
                                      trim-pointer-once]]
   [fif-test.utils :refer [are-eq* teval]]))


(deftest test-arg-is-pointer?
  (are-eq*
    (arg-is-pointer? '*) => false

    (arg-is-pointer? '*x) => true

    (arg-is-pointer? '**xx) => true

    (arg-is-pointer? '*test-value) => true

    (arg-is-pointer? 'test-value*) => false

    (arg-is-pointer? 't*) => false

    (arg-is-pointer? 1) => false

    (arg-is-pointer? "TEST") => false

    (arg-is-pointer? {:a '+}) => false

    (arg-is-pointer? '[*a]) => false

    (arg-is-pointer? #{'+}) => false

    (arg-is-pointer? '('A)) => false))


(deftest test-trim-pointer-once
  (are-eq*
    (trim-pointer-once '*) => '*

    (trim-pointer-once '*x) => 'x

    (trim-pointer-once 'test-value) => 'test-value

    (trim-pointer-once '**test-value) => '*test-value

    (trim-pointer-once [1 2 3]) => [1 2 3]))
