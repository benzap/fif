(ns example.interop-clojure
  "An example extending fif from clojure"
  (:require
   [fif.stack :as stack]
   [fif.core :as fif]))


(fif/reval 1 #=(+ 1 1) +) ;; => '(3)


(defn boiling-point-c [] 100)


(fif/reval #=(boiling-point-c) 1 +) ;; => '(101)


(defn secret-stack-machine
  "Returns a stack machine with a `secret` value stored in the fif
  variable 'secret"
  [secret]
  (-> fif.core/*default-stack*
      (stack/set-variable 'secret secret)))


(fif/with-stack (secret-stack-machine :fooey)
  (fif/reval secret getv)) ;; => (:fooey)


(defn pill-popping-stack-machine
  "Returns a stack machine with the values within `pills` places on
  the stack"
  [& pills]
  (loop [sm fif.core/*default-stack*
         pills pills]
    (if-let [pill (first pills)]
      (recur (stack/push-stack sm pill)
             (rest pills))
      sm)))


(fif/with-stack (pill-popping-stack-machine :red :black :green :blue)
  (fif/reval "The pill on the top of the stack is: " . .))
  ;; => '(:red :black :green)
  ;; <stdout>: The pill on the top of the stack is: :blue


(fif/reval-string "1 #=(+ 1 1) +")

(fif/reval-string "1 1 +")
