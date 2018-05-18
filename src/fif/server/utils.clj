(ns fif.server.utils
  (:import
   [java.io BufferedWriter PrintWriter Writer]))


;; Backported from clojure 1.10 clojure.core_print/PrinterWriter-on
(defn ^java.io.PrintWriter PrintWriter-on
  "implements java.io.PrintWriter given flush-fn, which will be called
  when .flush() is called, with a string built up since the last call to .flush().
  if not nil, close-fn will be called with no arguments when .close is called"
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
        java.io.PrintWriter.)))


(defmacro with-io [& body]
  `(let [sw-out# (new java.io.StringWriter)
         sw-err# (new java.io.StringWriter)]
     (binding [*out* sw-out#
               *err* sw-err#]
       (let [result# ~@body]
         {:result result#
          :out sw-out#
          :err sw-err#}))))


(def ^:private ^String system-newline
     (System/getProperty "line.separator"))
