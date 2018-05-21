(ns fif.stdlib.repl
  "Includes operations which are used primarily in a fif repl."
  (:require
   [clojure.string :as str]
   
   [fif.stack-machine.stash :as stack-machine.stash]
   [fif.stack-machine.words :as words]
   [fif.stack-machine.mode :as mode]
   [fif.stack-machine :as stack]))


(def arg-meta-op 'meta)
(def arg-see-op 'see)
(def arg-see-words-op 'see-words)
(def arg-see-groups-op 'see-groups)

(def arg-doc-op 'doc)
(def arg-group-op 'group)
(def see-mode-flag :see-mode)


(def doc-mode-flag :doc-mode)


(defn enter-doc-mode [sm state]
  (mode/enter-mode sm doc-mode-flag state))


(defn exit-doc-mode [sm]
  (mode/exit-mode sm))


(defmulti doc-mode mode/mode-dispatch-fn)


(defn doc-op [sm]
  (-> sm
      (enter-doc-mode {:state :retrieve-word})
      stack/dequeue-code))


(defmethod doc-mode
  {:state :retrieve-word}
  [sm]
  (let [wname (-> sm stack/get-code first)]
    (println "Wname" wname)
    (-> sm
        (mode/update-stash assoc ::wname wname)
        (mode/update-state assoc :state :retrieve-docstring)
        stack/dequeue-code)))


(defmethod doc-mode
  {:state :retrieve-docstring}
  [sm]
  (let [docstring (-> sm stack/get-code first)
        wname (-> sm mode/get-mode-stash ::wname)]
    (println "Doc" wname docstring)
    (-> sm
        (words/update-global-metadata wname assoc :doc docstring)
        exit-doc-mode
        stack/dequeue-code)))


(defn see-op [sm]
  (-> sm
      (stack/push-flag see-mode-flag)
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
    (println "group:\t" (:group meta))
    (println "type:\t" (cond
                         (= word words/not-found) (class arg)
                         (:variable? meta) "variable"
                         :else "function"))
    (println "doc:\t"  (:doc meta))
    (println "source:\t" (or (pr-str (:source meta)) "<clojure>"))
    (-> sm
        stack/pop-flag
        stack/dequeue-code)))


(defn import-stdlib-repl
  [sm]
  (-> sm

      (words/set-global-word-defn 
       arg-doc-op doc-op
       :doc "doc <string> -- set "
       :stdlib? true
       :group :stdlib.repl)

      (words/set-global-word-defn 
       arg-see-op see-op
       :doc "see <word> -- Display info about a given word definition."
       :stdlib? true
       :group :stdlib.repl)

      (words/set-global-word-defn
       arg-meta-op meta-op
       :doc "( word -- metadata ) Returns the metadata for the given word definition"
       :stdlib? true
       :group :stdlib.metadata)

      (stack/set-mode see-mode-flag see-mode)
      (stack/set-mode doc-mode-flag doc-mode)))
