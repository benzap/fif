(ns fif.stack-machine.words
  "Functions for realizing word functions and word variables."
  (:require
   [fif.stack-machine :as stack-machine]
   [fif.stack-machine.scope :as stack-machine.scope]))


(def not-found ::not-found)


(defn set-word
  [sm name wfunc]
  (stack-machine.scope/update-scope sm assoc-in [:words name] wfunc))


(defn get-word
  [sm name]
  (stack-machine.scope/get-in-scope sm [:words name] not-found))


(defn set-global-word
  [sm name wfunc]
  (stack-machine.scope/update-global-scope sm assoc-in [:words name] wfunc))


(defn get-global-word
  [sm name]
  (stack-machine.scope/get-in-global-scope sm [:words name] not-found))
