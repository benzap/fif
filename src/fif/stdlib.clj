(ns fif.stdlib
  "Includes all of the standard library functions for fif. These
  functions are stored as a mergable map to be used by any stack
  machine."
  (:refer-clojure :exclude [+])
  (:require [fif.stack :as stack :refer :all]
            [fif.def :refer []]))


(defn op+ [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core/+ i j)]
    (-> sm pop-stack pop-stack (push-stack result))))


(defn op- [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core/- i j)]
    (-> sm pop-stack pop-stack (push-stack result))))


(defn op* [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core/* i j)]
    (-> sm pop-stack pop-stack (push-stack result))))


(defn dup [sm]
  (let [top (-> sm get-stack peek)]
    (push-stack sm top)))


(defn dot [sm]
  (let [top (-> sm get-stack peek)]
    (println top)
    (-> sm
        pop-stack)))

#_(dot (-> (new-stack-machine) (push-stack 1)))

