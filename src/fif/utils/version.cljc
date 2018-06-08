(ns fif.utils.version)


(defn get-project-version []
  (System/getProperty "fif.version"))


(defn print-project-version []
  (println (get-project-version)))
