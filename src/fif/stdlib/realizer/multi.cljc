(ns fif.stdlib.realizer.multi
  (:require
   [fif.stack-machine :as stack-machine]
   [fif.stack-machine.processor :as processor]
   [fif.stack-machine.stash :as stack-machine.stash]
   [fif.stack-machine.words :refer [set-global-word-defn]]
   [fif.stack-machine.exceptions :as exceptions]
   [fif.stack-machine.mode :as mode]
   [fif.utils.token :as utils.token]))


(def arg-realize-token '??)
(def arg-realize-start-token '??/start)
(def arg-realize-finish-token '??/finish)
(def realize-multi-mode-flag :realize-multi-mode)


(defn enter-realize-multi-mode
  [sm state]
  (-> sm (mode/enter-mode realize-multi-mode-flag state)))


(defn exit-realize-multi-mode
  [sm]
  (-> sm (mode/exit-mode)))


(defmulti realize-multi-mode mode/mode-dispatch-fn)


(defmethod realize-multi-mode
  {:op ::?? :op-state ::init}
  [sm])


(defmethod realize-multi-mode
  {:op ::?? :op-state ::collect}
  [sm])


(defmethod realize-multi-mode
  {:op ::?? :op-state ::finish}
  [sm])


(def doc-string "<coll> ?? -- Realizes the collection, and any deeply nested collections")
(defn realize-multi-op
  [sm]
  (-> sm
      (enter-realize-multi-mode {:op ::?? :op-state ::init})))


(defn import-stdlib-realize-multi-mode
  [sm]
  (-> sm

      (set-global-word-defn
       arg-realize-token realize-multi-op
       :stdlib? true
       :doc doc-string
       :group :stdlib.realizer)

      (set-global-word-defn
       arg-realize-start-token exceptions/raise-unbounded-mode-argument
       :stdlib? true
       :doc doc-string
       :group :stdlib.realizer)

      (set-global-word-defn
       arg-realize-finish-token exceptions/raise-unbounded-mode-argument
       :stdlib? true
       :doc doc-string
       :group :stdlib.realizer)

      (stack-machine/set-mode realize-multi-mode-flag realize-multi-mode)))
