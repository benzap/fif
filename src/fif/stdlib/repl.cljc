(ns fif.stdlib.repl
  "Includes operations which are used primarily in a fif repl."
  (:require
   [clojure.string :as str]
   
   [fif.stack-machine.words :as words]
   [fif.stack-machine :as stack]))


(def arg-meta-op 'meta)
(def arg-see-op 'see)
(def arg-see-mode :see-mode)


(defn see-op [sm]
  (-> sm
      (stack/push-flag arg-see-mode)
      stack/dequeue-code))


(defn meta-op [sm]
  (let [[val] (stack/get-stack sm)
        meta (words/get-global-metadata sm val)]
    (-> sm
        stack/pop-stack
        (stack/push-stack meta)
        stack/dequeue-code)))


(defn see-mode
  [sm]
  (let [arg (-> sm stack/get-code first)
        word (-> sm (stack/get-word arg))
        meta (words/get-global-metadata sm arg)]
    (println "name:\t" arg)
    (println "type:\t" (cond
                         (= word words/not-found) (class arg)
                         (:variable? meta) "variable"
                         :else "function"))
    (println "doc:\t"  (:doc meta))
    (println "source:\t" (or (:source meta) "<clojure>"))
    (-> sm
        stack/pop-flag
        stack/dequeue-code)))


(defn import-stdlib-repl
  [sm]
  (-> sm
      (words/set-word-defn arg-see-op see-op
                          :doc "see <word> - Display info about a given word definition"
                          :stdlib? true)
      (words/set-word-defn arg-meta-op meta-op
                          :doc "(word -- metadata) Returns the metadata for the given word definition"
                          :stdlib? true)
      (stack/set-mode arg-see-mode see-mode)))
