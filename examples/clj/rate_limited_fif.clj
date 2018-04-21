(ns example.rate-limited-fif
  "An example of limited step execution"
  (:require
   [fif.stack-machine :as stack]
   [fif.core :as fif]))


(defn limited-stack-machine [step-max]
  (-> fif/*default-stack*
      (stack/set-step-max step-max)))


(def default-step-max 100)
(defn eval-incoming [s]
  (let [sm (limited-stack-machine default-step-max)
        evaluated-sm (fif/with-stack sm (fif/eval-string s))
        max-steps (stack/get-step-max evaluated-sm)
        num-steps (stack/get-step-num evaluated-sm)]
    (if (>= num-steps max-steps)
      "Exceeded Max Step Execution"
      (-> evaluated-sm stack/get-stack reverse))))


(def incoming-fif-eval "3 0 do :data-value i loop")
(eval-incoming incoming-fif-eval) ;; => (:data-value 0 :data-value 1 :data-value 2 :data-value 3)


(def infinite-fif-eval "begin true while :data-value 1 repeat")
(eval-incoming infinite-fif-eval) ;; => "Exceeded Max Step Execution"


(def malicious-fif-eval "begin #=(fork-main-thread) false until")
(eval-incoming malicious-fif-eval) ;; ERROR
;; Unhandled clojure.lang.ExceptionInfo
;; No reader function for tag =.
;; {:type :reader-exception, :ex-kind :reader-error}
