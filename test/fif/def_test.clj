(ns fif.def-test
  (:refer-clojure :exclude [eval])
  (:require
    [clojure.test :refer :all]
    [fif.stack :as stack]
    [fif.def :refer :all]
    [fif.core :as fif :refer [dbg-eval reval with-stack]]))


(def test-stack-machine (stack/new-stack-machine))


(defmacro deval
  "Includes the ability to add a max-step to prevent infinite loops"
  [max-step & body]
  `(->
    (dbg-eval {:max-step ~max-step} ~@body)
    fif.core/get-stack
    reverse))


(deftest test-wrap-function-with-arity
   (let [add2 (wrap-function-with-arity 1 #(+ 2 %))
         sm (-> test-stack-machine
                (stack/set-word 'add2 add2))]
     (is (= '(3) (with-stack sm (deval 100 1 add2))))))


(def ^:dynamic *test-val* nil)
(deftest test-wrap-procedure-with-arity
  (binding [*test-val* (atom 1)]
    (let [update-val! (fn [i] (reset! *test-val* i))
          op-update-val! (wrap-procedure-with-arity 1 update-val!)
          sm (-> test-stack-machine
                 (stack/set-word 'update-val! op-update-val!))
          _ (with-stack sm (reval 2 update-val!))]
      (is (= 2 @*test-val*)))))

(comment
  (def *secret-notes (atom []))
  (defn add-note! [s] (swap! *secret-ledger conj s))
  (defn get-notes [] @*secret-ledger)

  (add-note! "They're in the trees")
  (add-note! {:date "March 14, 2018" :name "Stephen Hawking"})

  (get-notes)) ;; => ["They're in the trees" {:date "March 14, 2018" :name "Stephen Hawking"}]
