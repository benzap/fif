(ns fif.server.utils)


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
