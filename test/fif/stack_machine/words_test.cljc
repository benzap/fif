(ns fif.stack-machine.words-test
  (:require
   [clojure.test :refer [deftest testing is are]]
   [fif.stack-machine.words :refer [set-word get-word
                                    set-global-word
                                    get-global-word
                                    not-found]]
   [fif.stack-machine.scope :as stack-machine.scope]
   [fif.impl.stack-machine :refer [new-stack-machine]]
   [fif-test.utils :refer [are-eq* teval]]))


(deftest test-words
  (are-eq*

   (-> (new-stack-machine)
       (stack-machine.scope/new-scope)
       (set-word 'test :value)
       (get-word 'test))

   => :value

   (-> (new-stack-machine)
       (stack-machine.scope/new-scope)
       (set-word 'test :value)
       (get-word 'test2))

   => not-found


   (-> (new-stack-machine)
       (stack-machine.scope/new-scope)
       (set-word 'test :value)
       (stack-machine.scope/new-scope)
       (get-word 'test))

   => :value


   (-> (new-stack-machine)
       (stack-machine.scope/new-scope)
       (set-word 'test :value)
       (stack-machine.scope/new-scope)
       (set-word 'test :value2)
       (get-word 'test))

   => :value2


   (-> (new-stack-machine)
       (stack-machine.scope/new-scope)
       (set-word 'test :value)
       (stack-machine.scope/new-scope)
       (set-word 'test :value2)
       (stack-machine.scope/remove-scope)
       (get-word 'test))

   => :value


   (-> (new-stack-machine)
       (stack-machine.scope/new-scope)
       (set-global-word 'test :value3)
       (set-word 'test :value)
       (stack-machine.scope/new-scope)
       (set-word 'test :value2)
       (stack-machine.scope/remove-scope)
       (get-global-word 'test))

   => :value3))
