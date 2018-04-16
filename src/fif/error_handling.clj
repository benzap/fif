(ns fif.error-handling
  "Functions for handling errors within the fif stackmachine."
  (:require
   [fif.stack :as stack]))


(def error-symbol 'ERR##)


(defn new-error-object
  "Creates a new error object"
  ([msg extra] [error-symbol msg extra])
  ([msg] (new-error-object msg {})))


(defn error? [obj]
  (-> obj first (= error-symbol)))


(defn stack-error
  "Creates an error object for stack errors"
  [sm msg extra]
  (new-error-object msg extra))


(defn stack-error? [obj]
  (error? obj))


(defn system-error
  "Creates an error object for system errors"
  [e msg]
  (new-error-object msg))
  

(defn system-error? [obj]
  (error? obj))


(defn set-error
  "Places the given error object on the stack, and attempts to halt the
  stack machine."
  [sm errobj]
  (-> sm
      (stack/push-stack errobj)
      stack/halt))
