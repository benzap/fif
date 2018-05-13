(ns fif.stack-machine.stash
  "Functions for manipulating the stack machine mode stash. The stash is
  used by individual modes for storing information in between steps."
  (:require
   [fif.stack-machine :as stack]
   [fif.utils.stash :as utils.stash]))


(defn get-stash
  "Gets the stash. Note that it is stash2, since this is going to
  replace the deprecated stash functionality which used 'sub-stacks'"
  [sm]
  (stack/get-mode-stash sm))


(defn set-stash
  "Set the stack-machine stash to the given stash."
  [sm stack]
  (stack/set-mode-stash sm stack))


(defn new-stash
  "Initialize a new stack-machine stash, or replace it with "
  ([sm coll]
   (let [stash (get-stash sm)]
     (set-stash sm (utils.stash/new-stash stash coll))))
  ([sm] (new-stash sm {})))


(defn clear-stash
  [sm]
  (set-stash sm (utils.stash/create-stash)))


(defn update-stash [sm f & args]
  (let [stash (get-stash sm)]
    (set-stash sm (apply utils.stash/update-stash stash f args))))


(defn remove-stash [sm]
  (let [stash (get-stash sm)]
    (set-stash sm (utils.stash/remove-stash stash))))


(defn peek-stash [sm]
  (let [stash (get-stash sm)]
    (utils.stash/peek-stash stash)))


