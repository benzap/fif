(ns fif.stack.stash-test
  (:require
   [clojure.test :refer :all]
   [fif.stack :as stack]
   [fif-test.utils :refer [are-eq* teval]]
   [fif.stack.stash :refer :all]))


(deftest test-stash
  (are-eq*

   (-> (stack/new-stack-machine)
       (get-stash))

   => []


   (-> (stack/new-stack-machine)
       (new-stash)
       (get-stash))

   => [{}]


   (-> (stack/new-stack-machine)
       (new-stash [])
       (get-stash))

   => [[]]


   (-> (stack/new-stack-machine)
       (new-stash)
       (update-stash assoc :test 123)
       (get-stash))

   => [{:test 123}]


   (-> (stack/new-stack-machine)
       (new-stash)
       (update-stash assoc :test 123)
       (new-stash)
       (update-stash assoc :test 456)
       (peek-stash))

   => {:test 456}


   (-> (stack/new-stack-machine)
       (new-stash)
       (update-stash assoc :test 123)
       (new-stash)
       (update-stash assoc :test 456)
       (remove-stash)
       (peek-stash))

   => {:test 123}))
