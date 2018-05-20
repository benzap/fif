(ns fif.stdlib.functional-ops
  "Includes operations for performing basic functional programming"
  (:require
   [fif.stack-machine :as stack-machine]
   [fif.stack-machine.processor :as processor]
   [fif.stack-machine.stash :as stack-machine.stash]
   [fif.stack-machine.mode :as mode]
   [fif.stack-machine.words :refer [set-global-word-defn]]
   [fif.stack-machine.exceptions :as exceptions]
   [fif.stdlib.reserved :as reserved]
   [fif.stdlib.conditional :refer [condition-true?]]
   [fif.utils.token :as utils.token]))


(def arg-start-reduce-token 'reduce)
(def arg-iter-reduce-token 'reduce/next-iteration)

(def arg-start-map-token 'map)
(def arg-iter-map-token 'map/next-iteration)

(def arg-start-filter-token 'filter)
(def arg-iter-filter-token 'filter/next-iteration)

(def functional-mode-flag :functional-mode)


(defn prepare-rfunction
  "Determines whether the function provided to the map-reduce-filter
  operations is just a sequence. The `rfunction` is converted such
  that a sequence can be used in place of a function within
  map-reduce-filter."
  [rfunction]
  (if (seq? rfunction) rfunction [rfunction]))


(defn enter-functional-mode [sm state]
  (mode/enter-mode sm functional-mode-flag state))


(defn exit-functional-mode [sm]
  (mode/exit-mode sm))


(defmulti functional-mode mode/mode-dispatch-fn)


;; Reduce Function


(defn reduce-op [sm]
  (-> sm
      (enter-functional-mode {:op ::reduce :op-state ::init})))


(defmethod functional-mode
  {:op ::reduce :op-state ::init}
  [sm]
  (let [[collection rfunction] (-> sm stack-machine/get-stack)]
    (-> sm
        (stack-machine.stash/update-stash assoc
                                          ::rfunction rfunction
                                          ::collection (rest collection)
                                          ::result (first collection))
        (mode/update-state assoc :op-state ::next-iteration)
        stack-machine/dequeue-code
        (stack-machine/update-code #(concat %2 %1) [arg-iter-reduce-token])
        stack-machine/pop-stack
        stack-machine/pop-stack)))


(defmethod functional-mode
  {:op ::reduce :op-state ::next-iteration}
  [sm]
  (let [stash (stack-machine.stash/peek-stash sm)
        rfunction (::rfunction stash)
        collection (::collection stash)
        result (::result stash)]
    (cond
      (empty? collection)
      (-> sm
          (mode/update-state assoc :op-state ::finish))
      :else
      (-> sm
          (stack-machine/push-stack result)
          (stack-machine/push-stack (first collection))
          (stack-machine.stash/update-stash assoc ::collection (rest collection))
          (mode/update-state assoc :op-state ::iterate)
          (stack-machine/update-code #(concat %2 %1) (prepare-rfunction rfunction))))))


(defmethod functional-mode
  {:op ::reduce :op-state ::iterate}
  [sm]
  (let [[top] (stack-machine/get-stack sm)
        arg (-> sm stack-machine/get-code first)]
    (cond
      (= arg arg-iter-reduce-token)
      (-> sm
          (stack-machine.stash/update-stash assoc ::result (-> sm stack-machine/get-stack peek))
          (mode/update-state assoc :op-state ::next-iteration)
          stack-machine/pop-stack)

      :else
      (-> sm processor/process-arg))))


(defmethod functional-mode
  {:op ::reduce :op-state ::finish}
  [sm]
  (let [result (-> sm stack-machine.stash/peek-stash ::result)]
    (-> sm
        (stack-machine/push-stack result)
        exit-functional-mode
        (stack-machine/dequeue-code))))


;; Map Function


(defn map-op [sm]
  (-> sm
      (enter-functional-mode {:op ::map :op-state ::init})))


(defmethod functional-mode
  {:op ::map :op-state ::init}
  [sm]
  (let [[collection rfunction] (-> sm stack-machine/get-stack)]
    (-> sm
        (stack-machine.stash/update-stash assoc
                                          ::rfunction rfunction
                                          ::collection collection
                                          ::result '())
        (mode/update-state assoc :op-state ::next-iteration)
        stack-machine/dequeue-code
        (stack-machine/update-code #(concat %2 %1) [arg-iter-map-token])
        stack-machine/pop-stack
        stack-machine/pop-stack)))


(defmethod functional-mode
  {:op ::map :op-state ::next-iteration}
  [sm]
  (let [stash (stack-machine.stash/peek-stash sm)
        rfunction (::rfunction stash)
        collection (::collection stash)]
    (cond
      (empty? collection)
      (-> sm
          (mode/update-state assoc :op-state ::finish))
      :else
      (-> sm
          (stack-machine/push-stack (first collection))
          (stack-machine.stash/update-stash assoc ::collection (rest collection))
          (mode/update-state assoc :op-state ::iterate)
          (stack-machine/update-code #(concat %2 %1) (prepare-rfunction rfunction))))))


(defmethod functional-mode
  {:op ::map :op-state ::iterate}
  [sm]
  (let [[top] (stack-machine/get-stack sm)
        arg (-> sm stack-machine/get-code first)
        result (-> sm stack-machine.stash/peek-stash ::result)]
    (cond
      (= arg arg-iter-map-token)
      (-> sm
          (stack-machine.stash/update-stash assoc ::result 
                                            (concat result [(-> sm stack-machine/get-stack peek)]))
          (mode/update-state assoc :op-state ::next-iteration)
          stack-machine/pop-stack)

      :else
      (-> sm processor/process-arg))))


(defmethod functional-mode
  {:op ::map :op-state ::finish}
  [sm]
  (let [result (-> sm stack-machine.stash/peek-stash ::result)]
    (-> sm
        (stack-machine/push-stack result)
        exit-functional-mode
        (stack-machine/dequeue-code))))


;; Filter Function


(defn filter-op [sm]
  (-> sm
      (enter-functional-mode {:op ::filter :op-state ::init})))


(defmethod functional-mode
  {:op ::filter :op-state ::init}
  [sm]
  (let [[collection rfunction] (-> sm stack-machine/get-stack)]
    (-> sm
        (stack-machine.stash/update-stash assoc
                                          ::rfunction rfunction
                                          ::collection collection
                                          ::current-value nil
                                          ::result '())
        (mode/update-state assoc :op-state ::next-iteration)
        stack-machine/dequeue-code
        (stack-machine/update-code #(concat %2 %1) [arg-iter-filter-token])
        stack-machine/pop-stack
        stack-machine/pop-stack)))


(defmethod functional-mode
  {:op ::filter :op-state ::next-iteration}
  [sm]
  (let [stash (stack-machine.stash/peek-stash sm)
        rfunction (::rfunction stash)
        collection (::collection stash)]
    (cond
      (empty? collection)
      (-> sm
          (mode/update-state assoc :op-state ::finish))
      :else
      (-> sm
          (stack-machine/push-stack (first collection))
          (stack-machine.stash/update-stash assoc ::collection (rest collection)
                                                  ::current-value (first collection))
          (mode/update-state assoc :op-state ::iterate)
          (stack-machine/update-code #(concat %2 %1) (prepare-rfunction rfunction))))))


(defmethod functional-mode
  {:op ::filter :op-state ::iterate}
  [sm]
  (let [[top] (stack-machine/get-stack sm)
        arg (-> sm stack-machine/get-code first)
        result (-> sm stack-machine.stash/peek-stash ::result)
        value (-> sm stack-machine.stash/peek-stash ::current-value)]
    (cond
      (= arg arg-iter-filter-token)
      (-> sm
          (as-> $
              (if top
                (stack-machine.stash/update-stash 
                 $ assoc ::result (concat result [value]))
                $))
          (mode/update-state assoc :op-state ::next-iteration)
          stack-machine/pop-stack)

      :else
      (-> sm processor/process-arg))))


(defmethod functional-mode
  {:op ::filter :op-state ::finish}
  [sm]
  (let [result (-> sm stack-machine.stash/peek-stash ::result)]
    (-> sm
        (stack-machine/push-stack result)
        exit-functional-mode
        (stack-machine/dequeue-code))))



(defn import-stdlib-functional-ops
  [sm]
  (-> sm
      (stack-machine/set-mode functional-mode-flag functional-mode)

      (set-global-word-defn
       arg-start-reduce-token reduce-op
       :stdlib? true
       :doc "<fn ( xs x -- 'xs )> <coll> reduce"
       :group :stdlib.functional)

      (set-global-word-defn
       arg-iter-reduce-token exceptions/raise-unbounded-mode-argument
       :stdlib? true
       :doc "<fn ( xs x -- 'xs )> <coll> reduce"
       :group :stdlib.functional)

      (set-global-word-defn
       arg-start-map-token map-op
       :stdlib? true
       :doc "<fn ( item -- 'item )> <coll> map"
       :group :stdlib.functional)

      (set-global-word-defn
       arg-iter-map-token exceptions/raise-unbounded-mode-argument
       :stdlib? true
       :doc "<fn ( item -- 'item )> <coll> map"
       :group :stdlib.functional)

      (set-global-word-defn
       arg-start-filter-token filter-op
       :stdlib? true
       :doc "<fn ( item -- boolean )> <coll> filter"
       :group :stdlib.functional)

      (set-global-word-defn
       arg-iter-filter-token exceptions/raise-unbounded-mode-argument
       :stdlib? true
       :doc "<fn ( item -- boolean )> <coll> filter"
       :group :stdlib.functional)))
