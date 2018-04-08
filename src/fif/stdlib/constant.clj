(ns fif.stdlib.constant
  (:require [fif.stack :as stack]))


(def arg-constant-token 'constant)
(def constant-mode-flag :constant-mode)


(defn wrap-word-constant
  [cval]
  (fn [sm]
    (-> sm (stack/push-stack cval) stack/dequeue-code)))


(defn constant-mode
  [sm arg]
  (let [[cval] (stack/get-stack sm)]
    (-> sm
      (stack/pop-stack)
      (stack/set-word arg (wrap-word-constant cval))
      (stack/pop-flag))))


(defn start-constant
  [sm]
  (-> sm
      (stack/push-flag constant-mode-flag)
      stack/dequeue-code))


(defn import-stdlib-constant-mode [sm]
  (-> sm
      (stack/set-word arg-constant-token start-constant)
      (stack/set-mode constant-mode-flag constant-mode)))
    
