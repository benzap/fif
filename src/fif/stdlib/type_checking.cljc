(ns fif.stdlib.type-checking
  (:require
   [fif.stack-machine :as stack]
   [fif.stack-machine.evaluators :as evaluators]
   [fif.stack-machine.words :as words :refer [set-global-word-defn]]
   [fif.def :as def
    :refer [wrap-function-with-arity
            wrap-procedure-with-arity]
    :include-macros true]))


(defn word?-op
  [sm]
  (let [[wname] (stack/get-stack sm)]
    (if (-> sm words/get-word-listing (get wname))
      (-> sm stack/pop-stack (stack/push-stack true) stack/dequeue-code)
      (-> sm stack/pop-stack (stack/push-stack false) stack/dequeue-code))))


(defn fn?-op
  [sm]
  (let [[wname] (stack/get-stack sm)]
    (if-let [meta (words/get-global-metadata sm wname)]
      (if (:variable? meta)
        (-> sm stack/pop-stack (stack/push-stack false) stack/dequeue-code)
        (-> sm stack/pop-stack (stack/push-stack true) stack/dequeue-code))
      (-> sm stack/pop-stack (stack/push-stack false) stack/dequeue-code))))

(defn variable?-op
  [sm]
  (let [[wname] (stack/get-stack sm)]
    (if-let [meta (words/get-global-metadata sm wname)]
      (if (:variable? meta)
        (-> sm stack/pop-stack (stack/push-stack true) stack/dequeue-code)
        (-> sm stack/pop-stack (stack/push-stack false) stack/dequeue-code))
      (-> sm stack/pop-stack (stack/push-stack false) stack/dequeue-code))))


(defn import-stdlib-type-checking [sm]
  (-> sm

      (set-global-word-defn
       'word? word?-op
       :stdlib? true
       :group :stdlib.type-checking
       :doc "( any -- bool ) Returns true if the given value is a word definition.")

      (set-global-word-defn
       'fn? fn?-op
       :stdlib? true
       :group :stdlib.type-checking
       :doc "( any -- bool ) Returns true if the given value is a word function.")

      (set-global-word-defn
       'variable? variable?-op
       :stdlib? true
       :group :stdlib.type-checking
       :doc "( any -- bool ) Returns true if the given value is a word variable.")))
