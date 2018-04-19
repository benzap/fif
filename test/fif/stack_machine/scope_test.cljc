(ns fif.stack-machine.scope-test
 (:require
  [clojure.test :refer [deftest testing is are]]
  [fif-test.utils :refer [are-eq* teval]]
  [fif.stack-machine :as stack]
  [fif.impl.stack-machine :refer [new-stack-machine]]
  [fif.stack-machine.scope :refer [get-scope
                                   new-scope
                                   update-scope
                                   get-in-scope
                                   remove-scope
                                   update-global-scope
                                   get-in-global-scope]]))
  

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

   => [{:words {}} {:words {'+ true}}]

   (-> (new-stack-machine)
       (new-scope)
       (update-scope assoc-in [:words 'test] 123)
       (update-scope update-in [:words] assoc '+ true)
       (new-scope)
       (update-scope assoc :words {})
       (update-scope update-in [:words] assoc '+ false)
       (update-scope assoc-in [:words 'test] 456)
       (get-in-scope [:words 'test]))
       

   => 456

   (-> (new-stack-machine)
       (new-scope)
       (update-global-scope assoc-in [:words 'test] 789)
       (update-scope assoc-in [:words 'test] 123)
       (update-scope update-in [:words] assoc '+ true)
       (new-scope)
       (update-scope assoc :words {})
       (update-scope update-in [:words] assoc '+ false)
       (update-scope assoc-in [:words 'test] 456)
       (get-in-global-scope [:words 'test]))
       

   => 789))


