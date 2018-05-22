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


(defn get-word-listing
  "Merges the words from each scope into a single map consisting of
  all word key and word definition value pairs."
  [sm]
  (->> (stack-machine.scope/get-scope sm)
       (map :words)
       (reduce merge)))


(defn get-metadata-listing
  "Merges the metadata from each scope into a single map consistingof
  all word key with word metadata value pairs."
  [sm]
  (->> (stack-machine.scope/get-scope sm)
       (map :word-metadata)
       (reduce merge)))


(defn set-metadata
  "Sets the word metadata in the local scope."
  [sm wname wmeta]
  (stack-machine.scope/update-scope sm assoc-in [:word-metadata wname] wmeta))


(defn get-metadata
  "Gets the word metadata in the local scope."
  [sm wname]
  (stack-machine.scope/get-in-scope sm [:word-metadata wname]))


(defn update-metadata
  [sm wname f & args]
  (if-let [meta (get-metadata sm wname)]
    (set-metadata sm wname (apply f meta args))
    (set-metadata sm wname (apply f {} args))))


(defn set-global-metadata
  "Sets metadata for the given `wname` which is an independent
  data-store containing information on the word definition defined by
  `wname` in the form of `wmeta`. `wmeta` should be a map of key
  values describing the word definition."
  [sm wname wmeta]
  (stack-machine.scope/update-global-scope sm assoc-in [:word-metadata wname] wmeta))


(defn get-global-metadata
  "Gets the metadata for the given `wname` word definition."
  [sm wname]
  (stack-machine.scope/get-in-global-scope sm [:word-metadata wname]))


(defn update-global-metadata
  [sm wname f & args]
  (if-let [meta (get-global-metadata sm wname)]
    (set-global-metadata sm wname (apply f meta args))
    (set-global-metadata sm wname (apply f {} args))))


(defn set-global-word
  "Sets a word definition within the global scope."
  [sm name wfunc]
  (stack-machine.scope/update-global-scope sm assoc-in [:words name] wfunc))


(defn get-global-word
  "Gets a word definition within the global scope."
  [sm name]
  (stack-machine.scope/get-in-global-scope sm [:words name] not-found))


(defn set-global-meta
  "Sets the metadata for a particular global word definition, which
  consists of the keys :doc, :source, :stdlib? and :variable?.

  Meta Data Keys:

  :doc - Is a string explaining the given word definition.

  :source - If it is a word definition defined within fif, this will
  contain the word definition source code in the form of an EDN vector
  collectionconsisting of the source.
  
  :group - Set the word definition to a particular key group
  ex. :stdlib.collection

  :stdlib? - If true, the word definition is part of the fif standard
  libraries.

  :variable? - If true, the word definition is a single value data
  value or data collection."
  [sm wname
   & {:keys [doc source group stdlib? variable?]
      :or {doc nil source nil group nil stdlib? false variable? false}}]
  (cond-> sm
    doc (update-global-metadata wname assoc :doc doc)
    source (update-global-metadata wname assoc :source source)
    group (update-global-metadata wname assoc :group group)
    true (update-global-metadata wname assoc :name wname)
    true (update-global-metadata wname assoc :stdlib? stdlib?)
    true (update-global-metadata wname assoc :variable? variable?)))


(defn set-global-word-defn
  "Used to set a word definition function while conveniently allowing
  you to set the metadata as well."
  [sm wname wfunc
   & {:keys [doc source group stdlib? variable?]
      :or {doc nil
           source nil
           group nil
           stdlib? false
           variable? false}}]
  (-> sm
      (set-global-word wname wfunc)
      (set-global-meta wname
                       :doc doc
                       :source source
                       :group group
                       :stdlib? stdlib?
                       :variable? variable?)))


(defn set-local-meta
  "Sets the metadata for a particular global word definition, which
  consists of the keys :doc, :source, :stdlib? and :variable?.

  Meta Data Keys:

  :doc - Is a string explaining the given word definition.

  :source - If it is a word definition defined within fif, this will
  contain the word definition source code in the form of an EDN vector
  collectionconsisting of the source.
  
  :group - Set the word definition to a particular key group
  ex. :stdlib.collection

  :stdlib? - If true, the word definition is part of the fif standard
  libraries.

  :variable? - If true, the word definition is a single value data
  value or data collection."
  [sm wname
   & {:keys [doc source group stdlib? variable?]
      :or {doc nil source nil group nil stdlib? false variable? false}}]
  (cond-> sm
    doc (update-metadata wname assoc :doc doc)
    source (update-metadata wname assoc :source source)
    group (update-metadata wname assoc :group group)
    true (update-metadata wname assoc :name wname)
    true (update-metadata wname assoc :stdlib? stdlib?)
    true (update-metadata wname assoc :variable? variable?)))


(defn set-local-word-defn
  "Used to set a word definition function while conveniently allowing
  you to set the metadata as well."
  [sm wname wfunc
   & {:keys [doc source group stdlib? variable?]
      :or {doc nil
           source nil
           group nil
           stdlib? false
           variable? false}}]
  (-> sm
      (set-word wname wfunc)
      (set-local-meta wname
                      :doc doc
                      :source source
                      :group group
                      :stdlib? stdlib?
                      :variable? variable?)))
