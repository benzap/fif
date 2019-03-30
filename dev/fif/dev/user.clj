(ns fif.dev.user
  (:require
   [clojure.tools.namespace.repl :refer [refresh]]

   [fif.core :as fif]
   [fif.server.core-test :refer :all]))


(defn start-dev-server []
  (start-test-server))


(defn stop-dev-server []
  (stop-test-server))
