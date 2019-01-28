(ns fif.utils.display
  (:refer-clojure :exclude [PrintWriter-on])
  (:require
   [clojure.pprint :refer [pprint]])
  #?(:clj (:import [java.io BufferedWriter PrintWriter Writer])))


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


;; Backported from clojure 1.10 clojure.core_print/PrinterWriter-on
#?(:clj
   (defn ^java.io.PrintWriter PrintWriter-on
     "implements java.io.PrintWriter given flush-fn, which will be called
     when .flush() is called, with a string built up since the last call
     to .flush().  if not nil, close-fn will be called with no arguments
     when .close is called"
     [flush-fn close-fn]
     (let [sb (StringBuilder.)]
       (-> (proxy [Writer] []
             (flush []
               (when (pos? (.length sb))
                 (flush-fn (.toString sb)))
               (.setLength sb 0))
             (close []
               (.flush ^Writer this)
               (when close-fn (close-fn))
               nil)
             (write [str-cbuf off len]
               (when (pos? len)
                 (if (instance? String str-cbuf)
                   (.append sb ^String str-cbuf ^int off ^int len)
                   (.append sb ^chars str-cbuf ^int off ^int len)))))
           java.io.BufferedWriter.
           java.io.PrintWriter.))))
