(ns fif.stdlib.collecter
  "Mode for collecting values, and placing within a data structure"
  (:require [fif.stack :as stack]
            [fif.def :refer [defcode-eval]]
            [fif.stdlib.macro :refer [import-stdlib-macro-mode]]))


(def collecter-mode-flag :collecter-mode)
;; $<- ....
(def arg-end-collecter '|)
(def arg-start-collecter '<-|)


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


(defcode-eval import-collection-collector-defaults
  macro list<-| $-| () <-| |-$ endmacro
  macro map<-| $-| {} <-| |-$ endmacro
  macro vec<-| $-| [] <-| |-$ endmacro
  macro set<-| $-| #{} <-| |-$ endmacro)



(defn import-stdlib-collecter-mode
  [sm]
  (-> sm
      (stack/set-mode collecter-mode-flag collecter-mode)
      (stack/set-word arg-start-collecter start-collecter)
      import-stdlib-macro-mode ;; Macro Dependency
      import-collection-collector-defaults))
