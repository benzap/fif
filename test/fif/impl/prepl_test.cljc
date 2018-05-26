(ns fif.impl.prepl-test
  (:require
   [clojure.test :refer [deftest testing is are]]
   [clojure.string :as str]
   [fif.stack-machine :as stack]
   [fif.core :refer [*default-stack*]]
   [fif.impl.prepl :refer [prepl]]
   [fif-test.utils :refer [are-eq*]]))


(deftest prepl-test-println
  (let [*svalue (atom "")
        output-fn (fn [{:keys [value]}]
                    (swap! *svalue str (str/replace value #"\r\n" "\n")))
        sm (prepl *default-stack* "true 2 2 + println" output-fn)]
    
    (are-eq*

     @*svalue => (str "4\n")

     (-> sm stack/get-stack first) => true)))


(deftest prepl-test-print
  (let [*svalue (atom "")
        output-fn (fn [{:keys [value]}]
                    (swap! *svalue str (str/replace value #"\r\n" "\n")))
        sm (prepl *default-stack* "true 2 2 + print" output-fn)]
    
    (are-eq*

     @*svalue => (str "4")

     (-> sm stack/get-stack first) => true)))
