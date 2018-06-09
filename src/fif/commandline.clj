(ns fif.commandline
  "Used for the standalone fif executable."
  (:require
   [clojure.string :as str]
   [clojure.java.io :as io]
   [clojure.tools.cli :refer [parse-opts]]
   [fif.core :as fif]
   [fif.def :refer [set-word-variable]]
   [fif.repl]
   [fif.stdlib.io :refer [import-stdlib-io]])
  (:gen-class))


(def ^:dynamic *commandline-stack*
  (-> fif/*default-stack*
      import-stdlib-io))


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
     (fif/with-stack *commandline-stack*
       (fif/eval-string (str/join " " arguments))
       (flush))

     (> (count arguments) 0)
     (let [[filename & args] arguments
           sform (slurp filename)
           sm (set-word-variable *commandline-stack* '$vargs (vec args))]
       (fif/with-stack sm
         (fif/eval-string sform)
         (flush)))

     :else
     (fif.repl/repl *commandline-stack*))))
       
     

     
     
