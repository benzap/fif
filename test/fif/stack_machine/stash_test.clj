(ns fif.stack-machine.stash-test
  (:require
   [clojure.test :refer :all]
   [fif.stack-machine :as stack]
   [fif.impl.stack-machine :refer [new-stack-machine]]
   [fif-test.utils :refer [are-eq* teval]]
   [fif.stack-machine.stash :refer :all]))


(deftest test-stash
  (are-eq*

   (-> (new-stack-machine)
       (get-stash))

   => []


   (-> (new-stack-machine)
       (new-stash)
       (get-stash))

   => [{}]


   (-> (new-stack-machine)
       (new-stash [])
       (get-stash))

   => [[]]


   (-> (new-stack-machine)
       (new-stash)
       (update-stash assoc :test 123)
       (get-stash))

   => [{:test 123}]


   (-> (new-stack-machine)
       (new-stash)
       (update-stash assoc :test 123)
       (new-stash)
       (update-stash assoc :test 456)
       (peek-stash))

   => {:test 456}


   (-> (new-stack-machine)
       (new-stash)
       (update-stash assoc :test 123)
       (new-stash)
       (update-stash assoc :test 456)
       (remove-stash)
       (peek-stash))

   => {:test 123}))
