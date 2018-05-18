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


(defn repl-eval [sform]
  (let [server-session-key (:server-session-key *fif-session*)
        {:keys [out err result]}
        (with-io (server.session/eval-session! server-session-key sform))
        sout (str out)
        serr (str err)]
    (println "eval out: " sout)
    (println "eval err: " serr)
    (when-not (empty? sout) (write-out sout))
    (when-not (empty? serr) (write-out serr))))


(defn repl
  []
  (println "Started Fif Socket Repl")
  (let [{:keys [server-session-key]} *fif-session*]
    (println "Session " server-session-key)
    (repl-init)
    (repl-prompt)
    (loop []
     (when-let [server-session (get @server.session/*server-sessions server-session-key)]
       (let [sform (repl-read)]
         (repl-eval sform)
         (repl-prompt)
         (when-not (= sform "bye")
           (println "Loop")
           (recur)))))))
  
