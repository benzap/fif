(ns fif.stack-machine.stash
  "Functions for manipulating the stack machine stash"
  (:require
   [fif.stack-machine :as stack]
   [fif.utils.stash :as utils.stash]))


(defn get-stash [sm]
  (stack/get-stash2 sm))


(defn set-stash [sm stack]
  (stack/set-stash2 sm stack))


(defn new-stash
  ([sm coll]
   (let [stash (get-stash sm)]
     (set-stash sm (utils.stash/new-stash stash coll))))
  ([sm] (new-stash sm {})))


(defn update-stash [sm f & args]
  (let [stash (get-stash sm)]
    (set-stash sm (apply utils.stash/update-stash stash f args))))


(defn remove-stash [sm]
  (let [stash (get-stash sm)]
    (set-stash sm (utils.stash/remove-stash stash))))


(defn peek-stash [sm]
  (let [stash (get-stash sm)]
    (utils.stash/peek-stash stash)))
