(ns fif.stack.stash
  "Functions for manipulating the stack machine stash"
  (:require
   [fif.stack :as stack]
   [fif.utils.stash :as stash]))


(defn get-stash [sm]
  (stack/get-stash2 sm))


(defn set-stash [sm stack]
  (stack/set-stash2 sm stack))


(defn create-stash [sm]
  (let [stash (get-stash sm)]
    (set-stash sm (stash/create-stash stash))))


(defn update-stash [sm f & args]
  (let [stash (get-stash sm)]
    (set-stash sm (apply stash/update-stash stash f args))))


(defn remove-stash [sm]
  (let [stash (get-stash sm)]
    (set-stash sm (stash/remove-stash stash))))
