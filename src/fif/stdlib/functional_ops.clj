(ns fif.stdlib.functional-ops
  "Includes operations for performing basic functional programming"
  (:require
   [fif.stack-machine :as stack-machine]
   [fif.stack-machine.processor :as processor]
   [fif.stack-machine.stash :as stack-machine.stash]
   [fif.stack-machine.mode :as mode]
   [fif.stdlib.reserved :as reserved]
   [fif.utils.token :as utils.token]))


(def arg-start-reduce-token 'reduce)
(def arg-iter-reduce-token 'reduce/next-iteration)
(def functional-mode-flag :functional-mode)


(defn enter-functional-mode [sm state]
  (mode/enter-mode sm functional-mode-flag state))


(defn exit-functional-mode [sm]
  (mode/exit-mode sm))


(defmulti functional-mode mode/mode-dispatch-fn)


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
          (stack-machine/update-code #(concat %2 %1) [rfunction])))))


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


(defn import-stdlib-functional-ops
  [sm]
  (-> sm
      (stack-machine/set-mode functional-mode-flag functional-mode)
      (stack-machine/set-word arg-start-reduce-token reduce-op)))
