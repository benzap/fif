(ns fif.stdlib.repl
  "Includes operations which are used primarily in a fif repl."
  (:require
   [clojure.string :as str]
   
   [fif.stack-machine.words :as words]
   [fif.stack-machine :as stack]))


(def arg-see-op 'see)
(def arg-see-mode :see-mode)


(defn see-op [sm]
  (-> sm
      (stack/push-flag arg-see-mode)
      stack/dequeue-code))


(defn see-mode
  [sm]
  (let [arg (-> sm stack/get-code first)
        word (-> sm (stack/get-word arg))
        doc (meta word)]
    (println "name: " arg)
    (println "type: " (if-not (= word words/not-found) "word" (class arg)))
    (println "doc: " doc)
    (-> sm
        stack/pop-flag
        stack/dequeue-code)))

(defn import-stdlib-repl
  [sm]
  (-> sm
      (stack/set-word arg-see-op see-op)
      (stack/set-mode arg-see-mode see-mode)))
