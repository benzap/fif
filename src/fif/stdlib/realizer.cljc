(ns fif.stdlib.realizer
  (:require
   [fif.stack-machine :as stack-machine]
   [fif.stack-machine.processor :as processor]
   [fif.stack-machine.stash :as stack-machine.stash]
   [fif.stack-machine.mode :as mode]
   [fif.utils.token :as utils.token]))


(def arg-realize-token '?)
(def arg-realize-start-token '?/start)
(def arg-realize-finish-token '?/finish)
(def realize-mode-flag :realize-mode)


(defn enter-realize-mode
  [sm state]
  (-> sm (mode/enter-mode realize-mode-flag state)))


(defn exit-realize-mode
  [sm]
  (-> sm (mode/exit-mode)))


(defmulti realize-mode mode/mode-dispatch-fn)


(defmethod realize-mode
  {:op ::? :op-state ::init}
  [sm]
  (let [[collection] (-> sm stack-machine/get-stack)]
    (-> sm
        (stack-machine.stash/update-stash assoc ::collection-type (empty collection))
        (mode/update-state assoc :op-state ::collect)
        stack-machine/dequeue-code
        stack-machine/pop-stack
        (stack-machine/push-stack arg-realize-start-token)
        (stack-machine/update-code #(concat %2 %3 %1) collection [arg-realize-finish-token]))))


(defmethod realize-mode
  {:op ::? :op-state ::collect}
  [sm]
  (let [arg (-> sm stack-machine/get-code first)]
    (cond
      (= arg arg-realize-finish-token)
      (-> sm
          (mode/update-state assoc :op-state ::finish))

      :else
      (processor/process-arg sm))))
      

(defmethod realize-mode
  {:op ::? :op-state ::finish}
  [sm]
  (let [coll-type (-> sm stack-machine.stash/peek-stash ::collection-type)
        [realized-collection new-stack]
        (-> sm
            stack-machine/get-stack
            (utils.token/split-at-token arg-realize-start-token))
        realized-collection (->> realized-collection reverse (into coll-type))
        realized-collection (if (seq? realized-collection)
                              (reverse realized-collection)
                              realized-collection)]
    (-> sm
        (stack-machine/set-stack new-stack)
        (stack-machine/push-stack realized-collection)
        (exit-realize-mode)
        (stack-machine/dequeue-code))))


(defn realize-op
  [sm]
  (-> sm
      (enter-realize-mode {:op ::? :op-state ::init})))
  

(defn import-stdlib-realize-mode
  [sm]
  (-> sm
      (stack-machine/set-word arg-realize-token realize-op)
      (stack-machine/set-mode realize-mode-flag realize-mode)))