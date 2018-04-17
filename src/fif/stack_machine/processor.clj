(ns fif.stack-machine.processor
  (:require
   [fif.stack-machine :refer :all]
   [fif.stack-machine.pointer :as pointer]))


(defn has-flags? [sm]
  (not (empty? (get-flags sm))))


(defn process-mode [sm]
  (let [arg (-> sm get-code first)
        current-mode (peek (get-flags sm))]
    (if-let [modefn (-> sm :modes (get current-mode))]
      (modefn sm)
      (throw (ex-info "Unable to find mode function for flagged mode: " current-mode)))))


(defn process-arg [sm]
  (let [arg (-> sm get-code first)]
    (cond
      (symbol? arg)
      (if-let [wfn (-> sm get-words (get arg))]
        (wfn sm)
        (-> sm
            (push-stack (pointer/trim-pointer-once arg))
            dequeue-code))
      :else
      (-> sm
          (push-stack arg)
          dequeue-code))))