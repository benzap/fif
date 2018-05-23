(ns fif.utils.display
  (:require
   [clojure.pprint :refer [pprint]]))


#?(:cljs (def ^:dynamic *err* (js/goog.string.StringBuffer.)))


(defn print-err
  [& args]
  (binding [*out* *err*]
    (apply print args)))


(defn pr-err
  [& args]
  (binding [*out* *err*]
    (apply pr args)))


(defn println-err
  [& args]
  (binding [*out* *err*]
    (apply println args)))


(defn prn-err
  [& args]
  (binding [*out* *err*]
    (apply prn args)))


(defn pprint-err
  [obj]
  (pprint obj *err*))
