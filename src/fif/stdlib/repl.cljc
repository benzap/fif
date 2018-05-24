(ns fif.stdlib.repl
  "Includes operations which are used primarily in a fif repl."
  (:require
   [clojure.string :as str]
   
   [fif.stack-machine.stash :as stack-machine.stash]
   [fif.stack-machine.words :as words]
   [fif.stack-machine.mode :as mode]
   [fif.stack-machine :as stack]))


(def arg-meta-op 'meta)
(def arg-setmeta-op 'setmeta)

(def arg-see-op 'see)
(def arg-see-words-op 'see-words)
(def arg-see-user-words-op 'see-user-words)
(def arg-see-groups-op 'see-groups)

(def arg-doc-op 'doc)
(def arg-group-op 'group)
(def see-mode-flag :see-mode)
(def doc-mode-flag :doc-mode)
(def group-mode-flag :group-mode)


;;
;; Doc Mode
;;


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
    (-> sm
        (mode/update-stash assoc ::wname wname)
        (mode/update-state assoc :state :retrieve-docstring)
        stack/dequeue-code)))


(defmethod doc-mode
  {:state :retrieve-docstring}
  [sm]
  (let [docstring (-> sm stack/get-code first)
        wname (-> sm mode/get-mode-stash ::wname)]
    (-> sm
        (words/update-global-metadata wname assoc :doc docstring)
        exit-doc-mode
        stack/dequeue-code)))


;;
;; Group Mode
;;


(defn enter-group-mode [sm state]
  (mode/enter-mode sm group-mode-flag state))


(defn exit-group-mode [sm]
  (mode/exit-mode sm))


(defmulti group-mode mode/mode-dispatch-fn)


(defn group-op [sm]
  (-> sm
      (enter-group-mode {:state :retrieve-word})
      stack/dequeue-code))


(defmethod group-mode
  {:state :retrieve-word}
  [sm]
  (let [wname (-> sm stack/get-code first)]
    (-> sm
        (mode/update-stash assoc ::wname wname)
        (mode/update-state assoc :state :retrieve-groupkey)
        stack/dequeue-code)))


(defmethod group-mode
  {:state :retrieve-groupkey}
  [sm]
  (let [groupkey (-> sm stack/get-code first)
        wname (-> sm mode/get-mode-stash ::wname)]
    (-> sm
        (words/update-metadata wname assoc :group groupkey)
        exit-group-mode
        stack/dequeue-code)))


(defn meta-op [sm]
  (let [[val] (stack/get-stack sm)
        meta (words/get-metadata sm val)]
    (-> sm
        stack/pop-stack
        (stack/push-stack meta)
        stack/dequeue-code)))


(defn setmeta-op [sm]
  (let [[new-meta wname] (stack/get-stack sm)
        old-meta (words/get-global-metadata sm wname)]
    (-> sm
        stack/pop-stack
        stack/pop-stack
        (words/set-metadata wname (merge old-meta new-meta))
        stack/dequeue-code)))


;;
;; See Mode
;;


(defn see-op [sm]
  (-> sm
      (stack/push-flag see-mode-flag)
      stack/dequeue-code))


(defn see-mode
  [sm]
  (let [arg (-> sm stack/get-code first)
        word (-> sm (stack/get-word arg))
        meta (words/get-global-metadata sm arg)]
    (println "name:\t" arg)
    (println "group:\t" (:group meta))
    (println "type:\t" (cond
                         (= word words/not-found) #?(:clj (class arg) :cljs (try (type arg)
                                                                                 (catch js/Error e
                                                                                   nil)))
                         (:variable? meta) (case (:variable? meta)
                                             :local "local variable"
                                             :global "global variable"
                                             "variable")
                         :else "function"))
    (println "doc:\t"  (:doc meta))
    (println "source:\t" (if-some [source (:source meta)] (pr-str (:source meta)) "<clojure>"))
    (-> sm
        stack/pop-flag
        stack/dequeue-code)))


(defn see-words-op
  [sm]
  (let [words (-> sm words/get-word-listing keys)]
    (->> words (map str) sort (apply println))
    (stack/dequeue-code sm)))


(defn see-user-words-op
  [sm]
  (let [words (-> sm words/get-word-listing keys)
        metadata (-> sm words/get-metadata-listing)
        user-words
        (reduce
         ;; Remove any entries that are part of stdlib, and keep any
         ;; words without metadata
         (fn [wlist wname]
           (if-let [wmeta (words/get-metadata sm wname)]
             (if (:stdlib? wmeta) wlist (conj wlist wname))
             (conj wlist wname)))
         []
         words)]
    (->> user-words (map str) sort (apply println))
    (stack/dequeue-code sm)))


(defn see-groups-op
  [sm]
  (let [groups (->> sm words/get-metadata-listing
                    vals (map :group) distinct sort)]
     (doseq [group groups]
       (println group))
     (stack/dequeue-code sm)))


(defn see-user-groups-op
  [sm]
  (let [groups (->> sm words/get-metadata-listing
                    vals (filter #(not (get % :stdlib? false)))
                    (map :group) distinct sort)]
     (doseq [group groups]
       (println group))
     (stack/dequeue-code sm)))


;; TODO: use proper mode function
(defn dir-op
  [sm]
  (let [[dir group] (-> sm stack/get-code)
        words (as-> sm $ (words/get-metadata-listing $)
                   (map (fn [[k v]] (assoc v :word k)) $)
                   (group-by :group $)
                   (get $ group)
                   (map :word $)
                   (sort $))]
    (apply println words)
    (-> sm
        stack/dequeue-code
        stack/dequeue-code)))


(defn import-stdlib-repl
  [sm]
  (-> sm

      (words/set-global-word-defn 
       arg-doc-op doc-op
       :doc "doc <wname> <string> -- set docstring for given word definition."
       :stdlib? true
       :group :stdlib.repl)

      (words/set-global-word-defn 
       arg-group-op group-op
       :doc "group <wname> <group-key> -- set group for given word definition."
       :stdlib? true
       :group :stdlib.repl)

      (words/set-global-word-defn 
       arg-see-op see-op
       :doc "see <word> -- Display info about a given word definition."
       :stdlib? true
       :group :stdlib.repl)

      (words/set-global-word-defn
       arg-see-words-op see-words-op
       :doc "see-words -- Display a list of all word definitions."
       :stdlib? true
       :group :stdlib.repl)

      (words/set-global-word-defn
       arg-see-user-words-op see-user-words-op
       :doc "see-user-words -- Display all words that are not part of the standard library."
       :stdlib? true
       :group :stdlib.repl)

      (words/set-global-word-defn
       arg-see-groups-op see-groups-op
       :doc "see-groups -- Display all word definition groups."
       :stdlib? true
       :group :stdlib.repl)

      (words/set-global-word-defn
       'see-user-groups see-user-groups-op
       :doc "see-user-groups -- Display all user word definition groups."
       :stdlib? true
       :group :stdlib.repl)

      (words/set-global-word-defn
       'dir dir-op
       :doc "dir <group> -- Display all words that belong to the given group"
       :stdlib? true
       :group :stdlib.repl)

      (words/set-global-word-defn
       arg-meta-op meta-op
       :doc "( word -- metadata ) Returns the metadata for the given word definition."
       :stdlib? true
       :group :stdlib.metadata)

      (words/set-global-word-defn
       arg-setmeta-op setmeta-op
       :doc "( wname metadata -- ) Sets and merges the key values of `metadata` into the metadata of `wname`."
       :stdlib? true
       :group :stdlib.metadata)

      (stack/set-mode see-mode-flag see-mode)
      (stack/set-mode doc-mode-flag doc-mode)
      (stack/set-mode group-mode-flag group-mode)))
