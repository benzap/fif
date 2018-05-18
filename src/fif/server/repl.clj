(ns fif.server.repl
  "Represents a fif repl, which is used with the fif.server.core socket
  server."
  (:require
   [clojure.edn :as edn]
   [fif.server.dynamic :refer [*fif-session* *fif-in* *fif-out* *fif-err*]]
   [fif.server.session :as server.session]
   [fif.server.utils :refer [with-io]]))


(def EOF (Object.))

(def crn "\r\n")


(defn write-out [s]
  (.write *fif-out* s 0 (count s))
  (.flush *fif-out*))


(defn repl-init []
  (println "Init")
  (write-out "Fif Socket Repl"))


(defn repl-prompt []
  (println "Prompt")
  (write-out (str crn ":")))


(defn repl-read []
  (println "Read")
  (.readLine *fif-in*))


(defn repl-eval [s]
  (println "Eval:" s (count s))
  (write-out (str s)))


(defn repl
  []
  (println "Started Fif Socket Repl")
  (let [{:keys [server-session-key]} *fif-session*]
    (println "Session " server-session-key)
    (repl-init)
    (repl-prompt)
    (loop []
     (when-let [server-session (get @server.session/*server-sessions server-session-key)]
       ;; Echo Server Test
       (let [s (repl-read)]
         (repl-eval s)
         (repl-prompt)
         (when-not (= s "bye")
           (println "Loop")
           (recur)))))))
  
