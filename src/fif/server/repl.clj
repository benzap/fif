(ns fif.server.repl
  "Represents a fif repl, which is used with the fif.server.core socket
  server."
  (:require
   [clojure.edn :as edn]
   [fif.server.dynamic :refer [*fif-session* *fif-in* *fif-out* *fif-err*]]
   [fif.server.session :as server.session]
   [fif.server.utils :refer [with-io]]))


(def EOF (Object.))


(defn write-out [s]
  (.write *fif-out* s 0 (count s)))


(defn repl-init []
  (write-out "Fif Socket Repl"))


(defn repl-prompt []
  (write-out "\n:"))


(defn repl-read []
  (str (edn/read {:eof ""} *fif-in*)))


(defn repl-eval [s]
  (write-out s))


(defn repl
  []
  (let [{:keys [server-session-key]} *fif-session*]
    (repl-init)
    (repl-prompt)
    (loop []
     (when-let [server-session (get server.session/*server-sessions server-session-key)]
       ;; Echo Server Test
       (let [s (repl-read)]
         (repl-eval s)
         (repl-prompt)
         (when-not (= s "bye")
           (recur)))))))
   
  
