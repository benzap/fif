(ns fif.server.core-test
  (:require
   [fif.core :as fif]
   [fif.stack-machine :as stack]
   [fif.stack-machine.error-handling :as stack.error-handling]
   [fif.server.core :refer :all]))


(def test-server-name "TestRepl")
(def test-stack
  (-> fif/*default-stack*
      stack/enable-debug
      (stack/set-system-error-handler stack.error-handling/default-system-error-handler)))


(defn start-test-server []
  (start-socket-server test-stack test-server-name))


(defn stop-test-server []
  (stop-socket-server test-server-name))
