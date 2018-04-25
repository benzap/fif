(ns fif.stdlib.exceptions
  (:require
   [fif.stack-machine :as stack]
   [fif.stack-machine.error-handling :as error-handling]
   [fif.stack-machine.mode :as mode]))


(def arg-raise-exception-token 'raise-exception!)
(def arg-on-exception-token 'on-exception)


(def exception-mode-flag :exception-mode)


(defn enter-exception-mode [sm state]
  (mode/enter-mode sm exception-mode-flag state))


(defn exit-exception-mode [sm]
  (mode/exit-mode sm))


(defmulti exception-mode mode/mode-dispatch-fn)


(defmethod exception-mode
  {:op ::raise :op-state ::ignore-code}
  [sm]
  (let [arg (-> sm stack/get-code first)]
    (cond
      (= arg arg-on-exception-token)
      (-> sm
          exit-exception-mode
          stack/dequeue-code)
      :else
      stack/dequeue-code)))


(defn raise-exception [sm]
  (-> sm
      (enter-exception-mode {:op ::raise :op-state ::ignore-code})))


(defn raise-op [sm]
  (-> sm
      raise-exception
      stack/dequeue-code))


(defn on-exception-op [sm]
  (-> sm
      stack/dequeue-code
      stack/dequeue-code))



(defn import-stdlib-exceptions [sm]
  (-> sm
      (stack/set-mode exception-mode-flag exception-mode)
      (stack/set-word arg-raise-exception-token raise-op)
      (stack/set-word arg-on-exception-token on-exception-op)))
      
