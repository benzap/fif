(ns fif.stdlib.realizer.multi
  "Notes:

  - depends on 'apply' word function in the collectors stdlib
  "
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


(defn prepare-map-collection [m]
  (reduce
   (fn [xs [k v]]
     (let [bform (cond-> '()
                   true                       (concat [k])
                   (seq? k)                   (concat ['apply])
                   (or (coll? k) (symbol? k)) (concat ['??])
                   true                       (concat [v])
                   (seq? v)                   (concat ['apply])
                   (or (coll? v) (symbol? v)) (concat ['??])
                   true                       vec)]
       (concat xs [bform arg-realize-token])))
   []
   m))


(defn prepare-other-collection [m]
   (reduce
    (fn [xs x]
      (if (coll? x)
        (concat xs [x '??])
        (concat xs [x])))
    []
    m))


(defmethod realize-multi-mode
  {:op ::?? :op-state ::init}
  [sm]
  (let [[collection] (-> sm stack-machine/get-stack)
        coll-type (empty collection)
        collection
        (cond
          (map? collection) 
          (prepare-map-collection collection)
          (coll? collection)
          (prepare-other-collection collection)
          :else
          collection)]
    (if (coll? collection)
      (-> sm
          (stack-machine.stash/update-stash assoc ::collection-type coll-type)
          (mode/update-state assoc :op-state ::collect)
          stack-machine/dequeue-code
          stack-machine/pop-stack
          (stack-machine/push-stack arg-realize-start-token)
          (stack-machine/update-code #(concat %2 %3 %1) collection [arg-realize-finish-token]))
      (-> sm
          exit-realize-multi-mode
          stack-machine/dequeue-code))))


(defmethod realize-multi-mode
  {:op ::?? :op-state ::collect}
  [sm]
  (let [arg (-> sm stack-machine/get-code first)]
    (cond
      (= arg arg-realize-finish-token)
      (-> sm
          (mode/update-state assoc :op-state ::finish))

      :else
      (processor/process-arg sm))))


(defn fix-map-key-pairs
  [kp]
  (case (count kp)
   0 nil
   1 [(first kp) nil]
   2 kp
   [(first kp) (rest kp)]))


(defmethod realize-multi-mode
  {:op ::?? :op-state ::finish}
  [sm]
  (let [coll-type (-> sm stack-machine.stash/peek-stash ::collection-type)
        [realized-collection new-stack]
        (-> sm
            stack-machine/get-stack
            (utils.token/split-at-token arg-realize-start-token))
        realized-collection (if (map? coll-type) (keep fix-map-key-pairs realized-collection) realized-collection)
        realized-collection (->> realized-collection reverse (into coll-type))
        realized-collection (if (seq? realized-collection)
                              (reverse realized-collection)
                              realized-collection)]
    (-> sm
        (stack-machine/set-stack new-stack)
        (stack-machine/push-stack realized-collection)
        (exit-realize-multi-mode)
        (stack-machine/dequeue-code))))


(def doc-string "<coll> ?? -- Realizes the collection, and any nested collections")
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
