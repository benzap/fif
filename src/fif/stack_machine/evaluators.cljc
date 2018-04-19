(ns fif.stack-machine.evaluators
  "Functions for running and evaluating fif code within a stack machine."
  (:refer-clojure :exclude [eval])
  (:require 
   [clojure.tools.reader.edn :as edn]
   [fif.stack-machine :as stack]))


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
  (str "[" s "]"))


(defn eval-string
  [sm s]
  (->> s
       wrap-eval-string
       edn/read-string
       (eval-fn sm)))
