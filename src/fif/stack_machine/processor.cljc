(ns fif.stack-machine.processor
  (:require
   [fif.stack-machine :as stack]
   [fif.stack-machine.words :as stack-machine.words]
   [fif.stack-machine.pointer :as pointer]))


(defn has-flags? [sm]
  (not (empty? (stack/get-flags sm))))


(defn process-mode [sm]
  (let [arg (-> sm stack/get-code first)
        current-mode (peek (stack/get-flags sm))]
    (if-let [modefn (-> sm :modes (get current-mode))]
      (modefn sm)
      (throw (ex-info "Unable to find mode function for flagged mode: " current-mode)))))


(defn process-arg [sm]
  (let [arg (-> sm stack/get-code first)]
    (cond
      (symbol? arg)
      (let [wfn (stack/get-word sm arg)]
        (if-not (= wfn stack-machine.words/not-found)
          (wfn sm)
          (-> sm
              (stack/push-stack (pointer/trim-pointer-once arg))
              stack/dequeue-code)))
      :else
      (-> sm
          (stack/push-stack arg)
          stack/dequeue-code))))
