(ns fif.server.repl
  "Represents a fif repl, which is used with the fif.server.core socket
  server."
  (:require
   [clojure.edn :as edn]
   [clojure.string :as str]

   [fif.server.dynamic :refer [*fif-session* *fif-in* *fif-out* *fif-err*]]
   [fif.server.session :as server.session]
   [fif.server.utils :refer [with-io]]))


(def EOF (Object.))

(def crn "\r\n")


(defn remove-telnet-codes [s]
  (-> s
      (str/replace #"\^\[\[(A|B|C|D)" " ")))


(defmacro with-fif-io-bindings
  "Bind *out* and *err* to *fif-out* and *fif-err*"
  [& body]
  `(binding [*out* *fif-out*
             *err* *fif-err*]
     ~@body))


(defn write-out [s]
  (.write *fif-out* s 0 (count s))
  (.flush *fif-out*))


(defn repl-init []
  (write-out (str "Fif Repl\r\n" crn)))


(defn repl-prompt []
  (write-out (str "> ")))


(defn repl-read []
  (.readLine *fif-in*))


(defn repl-eval [sform]
  (let [server-session-key (:server-session-key *fif-session*)
        sform (remove-telnet-codes sform)]
    (with-fif-io-bindings (server.session/eval-session! server-session-key sform))))


(defn repl
  []
  (let [{:keys [server-session-key]} *fif-session*]
    (repl-init)
    (repl-prompt)
    (loop []
     (when-let [server-session (get @server.session/*server-sessions server-session-key)]
       (let [sform (repl-read)]
         (repl-eval sform)
         (repl-prompt)
         (when-not (= sform "bye")
           (recur)))))))
  
