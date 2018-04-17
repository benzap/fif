(ns fif.stack-machine.scope-test
 (:require
  [clojure.test :refer :all]
  [fif-test.utils :refer [are-eq* teval]]
  [fif.stack-machine :as stack]
  [fif.impl.stack-machine :refer [new-stack-machine]]
  [fif.stack-machine.scope :refer :all]))
  

(deftest test-scope
  (are-eq*

   (-> (new-stack-machine)
       (get-scope))

   => [{}]

   (-> (new-stack-machine)
       (new-scope)
       (get-scope))

   => [{} {}]

   (-> (new-stack-machine)
       (new-scope)
       (update-scope assoc :words {})
       (update-scope update-in [:words] assoc '+ true)
       (get-scope))

   => [{} {:words {'+ true}}]

   (-> (new-stack-machine)
       (new-scope)
       (update-scope assoc :words {})
       (update-scope update-in [:words] assoc '+ true)
       (new-scope)
       (update-scope assoc :words {})
       (update-scope update-in [:words] assoc '+ false)
       (get-in-scope [:words '+]))

   => false

   (-> (new-stack-machine)
       (new-scope)
       (update-scope assoc :words {})
       (update-scope update-in [:words] assoc '+ true)
       (new-scope)
       (update-scope assoc :words {})
       (update-scope update-in [:words] assoc '+ false)
       (remove-scope)
       (update-global-scope assoc :words {})
       (get-scope))

   => [{:words {}} {:words {'+ true}}]))

