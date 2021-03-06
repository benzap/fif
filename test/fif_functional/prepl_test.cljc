(ns fif-functional.prepl-test
  "Functional test involving:

  - Defining a word variable in a stack-machine

  - Defining word functions from a set of clojure functions

  - Creating a programmable repl, which captures standard output."
  (:require
   [clojure.test :refer [deftest is testing]]
   [clojure.string :as str]

   [fif.core :as fif]
   [fif.def]

   [fif-test.utils :refer [are-eq*]]))


;; Note taking example
(def version "0.1")


(def *secret-notes (atom []))


(defn add-note! [note]
  (swap! *secret-notes conj note))


(defn clear-notes! [] (reset! *secret-notes []))


(def note-stack-machine
  (-> fif/*default-stack*
      
      (fif.def/set-word-variable
       'version version
       :doc "Current Version of Note Taking Stack Machine."
       :group :note-taking)

      (fif.def/set-word-function
       'add-note! (fif.def/wrap-procedure-with-arity 1 add-note!)
       :doc "( any -- ) Add a note."
       :group :note-taking)

      (fif.def/set-word-function
       'get-notes (fif.def/wrap-function-with-arity 0 #(deref *secret-notes))
       :doc "( -- notes ) Get the secret notes."
       :group :note-taking)))


(def *note-sm (atom note-stack-machine))
(def *result (atom ""))


(defn output-fn
  [{:keys [tag value]}]
  (swap! *result str (str/replace value #"\r\n" "\n")))


(defn prepl-reset! []
  (reset! *note-sm note-stack-machine)
  (reset! *result ""))


(defn prepl [sinput]
  (prepl-reset!)
  (swap! *note-sm fif/prepl-eval sinput output-fn)
  [(-> @*note-sm fif/get-stack reverse) @*result])


(deftest basic-evaluation-test
  (are-eq*

   (prepl "2 2 +")

   => ['(4) ""]

   (prepl "2 2 + .")

   => ['() "4"]))


(deftest version-test
  (are-eq*

   (prepl "version")

   => ['("0.1") ""]))


(deftest notes-test
  (are-eq*

   (let [result (prepl "\"My First Note\" add-note! get-notes")]
     (clear-notes!)
     result)

   => ['(["My First Note"]) ""]))
