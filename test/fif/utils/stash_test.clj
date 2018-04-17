(ns fif.utils.stash-test
  (:require
   [clojure.test :refer :all]
   [fif.utils.stash :refer :all]
   [fif.stack :as stack]
   [fif-test.utils :refer [are-eq* teval]]))


(deftest test-create-stash
  (is (= [] (create-stash))))


(deftest test-stash
  (are-eq*
    (-> (create-stash)
        (new-stash)
        (update-stash assoc :test 123))

    => [{:test 123}]

    (-> (create-stash)
        (new-stash)
        (update-stash assoc :test 123)
        (new-stash)
        (update-stash assoc :test 345))

    => [{:test 123} {:test 345}]

    (-> (create-stash)
        (new-stash)
        (update-stash assoc :test 123)
        (new-stash)
        (update-stash assoc :test 345)
        (get-stash))

    => {:test 345}

    (-> (create-stash)
        (new-stash)
        (update-stash assoc :test 123)
        (new-stash)
        (update-stash assoc :test 345)
        (remove-stash)
        (get-stash))

    => {:test 123}

    (-> (create-stash)
        (new-stash)
        (update-stash assoc :test 123)
        (new-stash)
        (update-stash assoc :test 345)
        (remove-stash)
        (new-stash [])
        (update-stash conj 1)
        (get-stash))

    => [1]))

