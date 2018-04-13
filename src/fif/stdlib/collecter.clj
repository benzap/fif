(ns fif.stdlib.collecter
  "Mode for collecting values, and placing within a data structure"
  (:require [fif.stack :as stack]))


(def collecter-mode-flag :collecter-mode)
(def arg-start-collecter '<-$)
(def arg-end-collecter '$<-)


(defn collecter-mode
  [sm]
  (let [stack (stack/get-stack sm)
        arg (-> sm stack/get-code first)]
    (cond
      (= arg arg-end-collecter)
      (let [[new-collection-values new-stack] (stack/split-at-token stack arg-start-collecter)
            collection (into (peek new-stack) (reverse new-collection-values))
            new-stack (rest new-stack)]
        (-> sm
            stack/pop-flag
            (stack/set-stack (concat [collection] new-stack))
            stack/dequeue-code))

      :else
      (stack/process-arg sm))))


(defn start-collecter
  [sm]
  (-> sm
      (stack/push-flag collecter-mode-flag)
      (stack/push-stack arg-start-collecter)
      stack/dequeue-code))


(defn import-stdlib-collecter-mode
  [sm]
  (-> sm
      (stack/set-mode collecter-mode-flag collecter-mode)
      (stack/set-word arg-start-collecter start-collecter)))
