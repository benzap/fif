(ns fif.utils.scope-test
  (:require
   [clojure.test :refer [deftest testing is are]]
   [fif.utils.scope :refer [new-scope
                            update-scope
                            get-in-scope
                            remove-scope
                            update-global-scope
                            get-merged-scope]]
   [fif-test.utils :refer [teval are-eq*]]))


(deftest test-new-scope
  (are-eq*
   (new-scope) => [{}]
   
   (new-scope [{:test 123}]) => [{:test 123} {}]))


(deftest test-update-scope
  (are-eq*
    (-> (new-scope) (update-scope assoc :test 123))

    => [{:test 123}]

    (-> (new-scope)
        (update-scope assoc :test 123)
        (new-scope)
        (update-scope assoc :test 456))

    => [{:test 123} {:test 456}]))


(deftest test-get-in-scope
  (are-eq*
   (-> (new-scope)
       (update-scope assoc :test 123)
       (update-scope assoc :test2 234)
       (new-scope)
       (update-scope assoc :test 456)
       (get-in-scope [:test]))

   => 456

   (-> (new-scope)
       (update-scope assoc :test 123)
       (update-scope assoc :test2 234)
       (new-scope)
       (update-scope assoc :test 456)
       (get-in-scope [:test2]))

   => 234

   (-> (new-scope)
       (update-scope assoc :test 123)
       (update-scope assoc :test2 234)
       (new-scope)
       (update-scope assoc :test 456)
       (get-in-scope [:test3]))

   => nil

   (-> (new-scope)
       (update-scope assoc :test 123)
       (update-scope assoc :test2 234)
       (new-scope)
       (update-scope assoc :test 456)
       (get-in-scope [:test3] ::test))

   => ::test))


(deftest test-remove-scope
  (are-eq*
   (-> (new-scope)
       (update-scope assoc :test 123)
       (update-scope assoc :test2 234)
       (new-scope)
       (update-scope assoc :test 456)
       (remove-scope)
       (get-in-scope [:test] ::test))

   => 123))


(deftest test-update-global-scope
  (are-eq*
   (-> (new-scope)
       (update-scope assoc :test 123)
       (new-scope)
       (update-scope assoc :test 123)
       (update-global-scope assoc :test 345))

   => [{:test 345} {:test 123}]))


(deftest test-get-merged-scope
  (are-eq*
   (-> (new-scope)
       (update-scope assoc :test 123)
       (update-scope assoc :test2 234)
       (new-scope)
       (update-scope assoc :test 345)
       (get-merged-scope))

   => {:test 345 :test2 234}))
