(ns fif.server.core
  "A fif server uses a stack-machine with a set of *sessions*, which are
  separate stack-machine instantiations."
  (:require
   [clojure.string :as str]
   [fif.stack-machine.evaluators :refer [eval-string]]
   [fif.server.session :as server.session]
   [fif.server.prepl]
   [fif.server.repl]
   [fif.server.dynamic :refer [*fif-session* *fif-in* *fif-out* *fif-err*]]
   [fif.server.utils :refer [PrintWriter-on]])
  (:import
   [clojure.lang LineNumberingPushbackReader]
   [java.net Socket ServerSocket SocketException InetAddress]
   [java.io
    Reader Writer PrintWriter
    BufferedWriter BufferedReader
    InputStreamReader OutputStreamWriter]))


(defmacro ^:private thread
  "Instatiate a new thread with given `name`, running the form
  `body`. `daemon?` is true if the thread is a daemon."
  [^String name daemon? & body]
  `(doto (Thread. (fn [] ~@body) ~name)
    (.setDaemon ~daemon?)
    (.start)))


(def prepl fif.server.prepl/prepl)
(def repl fif.server.repl/repl)


(def *server-socket-instances
  "A socket server creates a thread to accept a socket connection. The
  socket connection is then handled by a new thread with the server's
  predefined repl-fn."
  (ref {}))


(defn run-server-session
  "function to run and evaluate a socket connection session."
  [{:keys [session-id
           socket-connection
           socket-in
           socket-out
           server-session-key
           server-name
           server-repl-fn
           server-repl-args]}]
  (try
    (when-let [server-session (-> @server.session/*server-sessions (get server-session-key))]
      (binding [*fif-session* {:server-name server-name
                               :server-session-key server-session-key}
                *fif-in* socket-in
                *fif-out* socket-out
                *fif-err* socket-out]
        (println "Client Connected... <" session-id ">")
        (apply server-repl-fn server-repl-args)))

    ;; Catch the socket disconnecting
    (catch SocketException _disconnect)
    (finally
      (dosync
       (println "Client Disconnected... <" session-id ">")
       (alter *server-socket-instances
              update-in [server-name :server-connections] dissoc session-id)))))


(defn socket-input-reader
  [socket-connection]
  (-> socket-connection
      .getInputStream
      InputStreamReader.
      LineNumberingPushbackReader.))


(defn socket-output-writer
  [socket-connection]
  (-> socket-connection
      .getOutputStream
      OutputStreamWriter.
      BufferedWriter.))


(defn run-server-socket-instance
  "Accepts new socket connections for the server-socket-instance `name`
  defined in `*server-socket-instances`.

  Notes:

  - new socket connections are managed by a newly generated thread"
  [name]
  (try
    (loop [session-id 0]
      (when-let [{:keys [stack-machine server-socket
                         server-address server-port
                         server-repl-fn server-repl-args]}
                 (get @*server-socket-instances name)]
        (when-not (.isClosed server-socket)
          (try
            (let [socket-connection (.accept server-socket)
                  socket-in (socket-input-reader socket-connection)
                  socket-out (socket-output-writer socket-connection)
                  server-session-key (server.session/new-session! stack-machine)
                  server-session
                  {:socket-connection socket-connection
                   :session-id session-id
                   :socket-in socket-in
                   :socket-out socket-out
                   :server-session-key server-session-key
                   :server-name name
                   :server-repl-fn server-repl-fn
                   :server-repl-args server-repl-args}]
              (dosync
               (alter *server-socket-instances
                      update-in [name :server-connections] assoc session-id server-session)
               
               ;; Create thread to handle connection
               (thread (str "<Fif Socket Server Connection> " name ":" session-id
                            " -- " server-address ":" server-port)
                       true
                       (run-server-session server-session))))

            ;; SocketException is determined to be a disconnect
            (catch SocketException _disconnect))
          (recur (inc session-id)))))
    (finally
      (dosync
       (alter *server-socket-instances dissoc name)))))


(defn new-server-instance
  [sm name address port repl-fn repl-args]
  ;; TODO: check if 'name' already has a server instance
  (let [address (InetAddress/getByName address)
        server-socket (ServerSocket. port 0 address)
        server-instance
        {:stack-machine sm
         :server-socket server-socket
         :server-address address
         :server-port port
         :server-name name
         :server-connections {}
         :server-repl-fn repl-fn
         :server-repl-args repl-args}]
    (dosync
     (alter *server-socket-instances assoc name server-instance)
     (thread (str "<Fif Socket Server> " name " -- " address ":" port)
             true
             (run-server-socket-instance name)))))
    


(defn start-socket-server
  [sm name & {:keys [address port repl-fn repl-args]
              :or {address "localhost" port 5001 repl-fn repl repl-args []}}]
  (new-server-instance sm name address port repl-fn repl-args))


(defn stop-socket-server
  "Stops the server instance with the given name"
  ([name]
   (if-let [{:keys [server-socket]} (get @*server-socket-instances name)]
     (dosync
      (println "Stopping Socket Repl: " name)
      (alter *server-socket-instances dissoc name)
      (.close server-socket))
     (println "Unable to find socket server: " name))
   @*server-socket-instances)
  ([] (when *fif-session* (stop-socket-server (get *fif-session* :server-name)))))
  

