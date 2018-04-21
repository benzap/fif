(ns example.extending-clojure
  "An example extending fif from clojure"
  (:require
   [fif.core :as fif]
   [fif.stack-machine :as stack]
   [fif.def :refer [wrap-function-with-arity
                    wrap-procedure-with-arity]]))


(def *secret-notes (atom []))
(defn add-note! [s] (swap! *secret-notes conj s))
(defn get-notes [] @*secret-notes)

(add-note! "They're in the trees")
(add-note! {:date "March 14, 2018" :name "Stephen Hawking"})

(get-notes) ;; => ["They're in the trees" {:date "March 14, 2018" :name "Stephen Hawking"}]

;; Wrap add-note! as a procedure which accepts 1 value from the
;; stack. Note that the procedure wrapper does not return the result
;; of our function to the stack.
(def op-add-note! (wrap-procedure-with-arity 1 add-note!))

;; Wrap get-notes as a function. Note that the function wrapper will
;; return its result to the stack.
(def op-get-notes (wrap-function-with-arity 0 get-notes))

(def extended-stack-machine
  (-> fif/*default-stack*
      (stack/set-word 'add-note! op-add-note!)
      (stack/set-word 'get-notes op-get-notes)))

;; Let's take our new functionality for a spin
(reset! *secret-notes [])
(fif/with-stack extended-stack-machine
  (fif/reval "I Hate Mondays" add-note!) ;; => '()
  (fif/eval-string "\"Kill Switch: Pineapple\" add-note!") ;; => '()
  (fif/reval get-notes)) ;; => '(["I Hate Mondays" "Kill Switch: Pineapple"])
