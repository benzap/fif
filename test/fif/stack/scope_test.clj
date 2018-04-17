(ns fif.stack.scope-test
 (:require
  [clojure.test :refer :all]
  [fif-test.utils :refer [are-eq* teval]]
  [fif.stack :as stack]
  [fif.stack.scope :refer :all]))
  

(deftest test-scope
  (are-eq*

   (-> (stack/new-stack-machine)
       (get-scope))

   => [{}]

   (-> (stack/new-stack-machine)
       (new-scope)
       (get-scope))

   => [{} {}]

   (-> (stack/new-stack-machine)
       (new-scope)
       (update-scope assoc :words {})
       (update-scope update-in [:words] assoc '+ true)
       (get-scope))

   => [{} {:words {'+ true}}]

   (-> (stack/new-stack-machine)
       (new-scope)
       (update-scope assoc :words {})
       (update-scope update-in [:words] assoc '+ true)
       (new-scope)
       (update-scope assoc :words {})
       (update-scope update-in [:words] assoc '+ false)
       (get-in-scope [:words '+]))

   => false))


