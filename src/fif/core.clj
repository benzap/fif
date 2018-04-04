(ns fif.core)



(def *symbols (atom {}))
(def *stack (atom []))


(defn push! [val]
  (swap! *stack conj val))


(defn pop! []
  (swap! *stack pop))


(defn eval! [])





