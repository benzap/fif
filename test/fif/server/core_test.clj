(ns fif.server.core-test
  (:require
   [fif.core :as fif]
   [fif.server.core :refer :all]))


(def test-server-name "TestRepl")


(defn start-test-server []
  (start-socket-server fif/*default-stack* test-server-name))


(defn stop-test-server []
  (stop-socket-server test-server-name))
