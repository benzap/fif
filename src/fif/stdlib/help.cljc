(ns fif.stdlib.help
  "Functions for learning about how to use fif."
  (:require
   [fif.stack-machine :as stack]
   [fif.stack-machine.words :as words]))


(def arg-help-op 'help)


(def ^:dynamic *help-msg*
  "
Fif Help Message
----------------
Website: github.com/benzap/fif

View all words with 'see-words'

View values on the stack with '.s'

Learn more about a specific word with 'see <word>'.

Words are categorized by group.
 - View all of the groups with 'see-groups'
 - View all of the words in a group with 'dir <group>'

")


(defn help-op
  [sm]
  (println *help-msg*)
  (stack/dequeue-code sm))


(defn import-stdlib-help
  [sm]

  (-> sm

      (words/set-global-word-defn
       'help help-op
       :stdlib? true
       :group :stdlib.help
       :doc "Display a help message.")))
