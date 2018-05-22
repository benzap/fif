(ns fif.server.session
  (:require
   [fif.stack-machine.evaluators :refer [eval-string]])
  (:import
   [java.util UUID]))


(def *server-sessions (ref {}))


(defn clear-sessions!
  "Clear all available server sessions"
  []
  (dosync
   (ref-set *server-sessions {})))


(defn get-stack-machine [key]
  (-> @*server-sessions (get key) :stack-machine))


(defn new-session!
  ([key sm]
   (let [opts {:stack-machine sm}]
     (dosync
      (alter *server-sessions assoc key opts)
      key)))
  ([sm] (new-session! (UUID/randomUUID) sm)))


(defn eval-session!
  [key sform]
  (dosync
   (alter *server-sessions update-in [key :stack-machine] eval-string sform)))


(defn remove-session!
  [key]
  (dosync
   (alter *server-sessions dissoc key)))
