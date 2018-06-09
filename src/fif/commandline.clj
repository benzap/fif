(ns fif.commandline
  "Used for the standalone fif executable."
  (:require
   [clojure.string :as str]
   [clojure.java.io :as io]
   [clojure.tools.cli :refer [parse-opts]]
   [fif.core :as fif]
   [fif.def :refer [set-word-variable]]
   [fif.repl])
  (:gen-class))


(def ^:dynamic *commandline-stack*
  fif/*default-stack*)


(def help-message
  "fif scripting language.

Usage:
  fif [options]
  fif <filename> [arguments..] [options]
  
Options:
  -h, --help         Show this screen.
  -e, --eval=<form>  Evaluate String Form
")


(def cli-options
 [["-h" "--help"]
  ["-e" "--eval"]])


(defn -main
  [& args]
  (let [{:keys [options arguments errors]}
        (parse-opts args cli-options)]
    (println "Options: " options)
    (println "Arguments: " arguments)
    (println "Errors: " errors)
    (cond
     (not (empty? errors))
     (do
       (println "CMDERROR")
       (doseq [errstr errors]
         (println errstr))
       (println)
       (println help-message))

     (:help options)
     (println help-message)

     (:eval options)
     (fif/eval-string (str/join " " arguments))

     (> (count arguments) 0)
     (let [[filename & args] arguments
           sform (slurp filename)
           sm (set-word-variable *commandline-stack* '$vargs (vec args))]
       (fif/with-stack sm
         (fif/eval-string sform)))

     :else
     (fif.repl/repl *commandline-stack*))))
       
     

     
     
