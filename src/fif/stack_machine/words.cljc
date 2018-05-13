(ns fif.stack-machine.words
  "Functions for realizing word functions and word variables."
  (:require
   [fif.stack-machine :as stack-machine]
   [fif.stack-machine.scope :as stack-machine.scope]))


(def not-found ::not-found)


(defn set-word
  "Sets a word definition within the current scope with the given
  `name`. `wfunc` is a function which takes the current stack-machine,
  and performs an operation within the given stack machine."
  [sm name wfunc]
  (stack-machine.scope/update-scope sm assoc-in [:words name] wfunc))


(defn get-word
  "Gets a word definition within the current scope of the
  stack-machine."
  [sm name]
  (stack-machine.scope/get-in-scope sm [:words name] not-found))


(defn set-global-metadata
  "Sets metadata for the given `wname` which is an indepedent data-store
  containing information on the word definition defined by `wname` in
  the form of `wmeta`. `wmeta` should be a map of key values
  describing the word definition."
  [sm wname wmeta]
  (assoc-in sm [:word-metadata wname] wmeta))


(defn get-global-metadata [sm wname]
  (get-in sm [:word-metadata wname]))


(defn set-global-word
  [sm name wfunc]
  (stack-machine.scope/update-global-scope sm assoc-in [:words name] wfunc))


(defn get-global-word
  [sm name]
  (stack-machine.scope/get-in-global-scope sm [:words name] not-found))


(defn set-meta
  [sm wname
   & {:keys [doc source stdlib? variable?]
      :or {doc nil source nil stdlib? false variable? false}}]
  (set-global-metadata sm wname {:doc doc :source source :stdlib? stdlib? :variable? variable?}))


(defn set-stdlib-meta
  [sm wname
   & {:keys [doc source variable?] :or {source nil variable? false}}]
  (set-meta sm wname :doc doc :source source :stdlib? true :variable? variable?))
