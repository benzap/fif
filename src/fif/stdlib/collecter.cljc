(ns fif.stdlib.collecter
  "Mode for collecting values, and placing within a data structure"
  (:require
   [fif.def :refer [defcode-eval] :include-macros true]
   [fif.stack-machine :as stack]
   [fif.stack-machine.processor :as stack.processor]
   [fif.stack-machine.words :refer [set-global-word-defn
                                    set-global-meta]]
   [fif.stack-machine.exceptions :as exceptions]
   [fif.utils.token :as token]
   [fif.stdlib.macro :refer [import-stdlib-macro-mode]]))


(def collecter-mode-flag :collecter-mode)
(def arg-start-collecter '<-into!)
(def arg-end-collecter '!)


(defn collecter-mode
  [sm]
  (let [stack (stack/get-stack sm)
        arg (-> sm stack/get-code first)]
    (cond
      (= arg arg-end-collecter)
      (let [[new-collection-values new-stack] (token/split-at-token stack arg-start-collecter)
            collection (into (peek new-stack) (reverse new-collection-values))
            new-stack (rest new-stack)]
        (-> sm
            stack/pop-flag
            (stack/set-stack (concat [collection] new-stack))
            stack/dequeue-code))

      :else
      (stack.processor/process-arg sm))))


(defn start-collecter
  [sm]
  (-> sm
      (stack/push-flag collecter-mode-flag)
      (stack/push-stack arg-start-collecter)
      stack/dequeue-code))


(defcode-eval import-collection-collecter-defaults
  macro list! _! () <-into! !_ endmacro
  macro map! _! {} <-into! !_ endmacro
  macro vec! _! [] <-into! !_ endmacro
  macro set! _! #{} <-into! !_ endmacro)



(defn import-stdlib-collecter-mode
  [sm]
  (-> sm
      (stack/set-mode collecter-mode-flag collecter-mode)

      (set-global-word-defn
       arg-start-collecter start-collecter
       :stdlib? true
       :doc "<coll> <-into! <body> ! -- Place <body> form into <coll> collection."
       :group :stdlib.mode.collecter)

      (set-global-word-defn
       arg-end-collecter exceptions/raise-unbounded-mode-argument
       :stdlib? true
       :doc "<coll> <-into! <body> ! -- Place <body> form into <coll> collection."
       :group :stdlib.mode.collecter)

      import-stdlib-macro-mode ;; fif Macro Dependency
      import-collection-collecter-defaults

      (set-global-meta 'list! :stdlib? true)
      (set-global-meta 'map! :stdlib? true)
      (set-global-meta 'vec! :stdlib? true)
      (set-global-meta 'set! :stdlib? true)))
