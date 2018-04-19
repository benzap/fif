(ns fif.utils.stash-test
  (:require
   [clojure.test :refer :all]
   [fif.utils.stash :refer :all]
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
        (peek-stash))

    => {:test 345}

    (-> (create-stash)
        (new-stash)
        (update-stash assoc :test 123)
        (new-stash)
        (update-stash assoc :test 345)
        (remove-stash)
        (peek-stash))

    => {:test 123}

    (-> (create-stash)
        (new-stash)
        (update-stash assoc :test 123)
        (new-stash)
        (update-stash assoc :test 345)
        (remove-stash)
        (new-stash [])
        (update-stash conj 1)
        (peek-stash))

    => [1]))
