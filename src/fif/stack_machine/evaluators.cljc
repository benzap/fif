(ns fif.stack-machine.evaluators
  "Functions for running and evaluating fif code within a stack machine."
  (:refer-clojure :exclude [eval read-string])
  (:require 
   [clojure.tools.reader.edn :as edn]
   
   [fif.stack-machine :as stack]
   [fif.stack-machine.error-handling :as stack.error-handling])
  #?(:clj
     (:import [clojure.lang ExceptionInfo])))

(defn eval-fn
  [sm args]
  (-> sm 
      (stack/set-code (concat (stack/get-code sm) args))
      stack/run))


(defmacro eval
  [sm & body]
  `(eval-fn ~sm (quote ~body)))


(defn wrap-eval-string
  [s]
  (str "\n[\n" s "\n]\n"))


(defn read-string [sm s]
  (let [s (wrap-eval-string s)]
    (try
      [sm (edn/read-string s)]
      
      ;; Parsing Error
      (catch #?(:clj ExceptionInfo :cljs js/Error) ex
        [(stack.error-handling/handle-system-error sm ex) []]))))


(defn eval-string
  [sm s]
  (let [[sm args] (read-string sm s)]
       (eval-fn sm args)))
