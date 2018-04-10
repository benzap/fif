(ns fif.stdlib.compile
  "Defines the compile-mode within the fif stack machine"
  (:require [fif.stack :as stack]))


(def arg-start-token 'fn)
(def arg-end-token 'endfn)
(def compile-mode-flag :compile-mode)
(def inner-compile-mode-flag :inner-compile-mode)


(defn wrap-compiled-fn [wbody]
  (fn [sm]
    (stack/set-code sm (concat wbody (-> sm stack/dequeue-code stack/get-code)))))


(defn inner-compile-mode
  [sm])



(defn compile-mode
  "This should only be called while we are in compile mode."
  [sm]
  (let [arg (-> sm stack/get-code first)]
    (if-not (= arg arg-end-token)
      (-> sm (stack/push-stack arg) stack/dequeue-code)
      (let [stack (stack/get-stack sm)
            fn-content (reverse (stack/take-to-token stack arg-start-token))
            [wname & wbody] fn-content]
        (as-> sm $
          (stack/set-word $ wname (wrap-compiled-fn wbody))
          (reduce (fn [sm f] (f sm)) $ (repeat (inc (count fn-content)) stack/pop-stack))
          (stack/pop-flag $)
          (stack/dequeue-code $))))))


(defn start-defn
  "We retrieved the start-token word, and we push it onto the stack and
   set our compile flag"
  [sm]
  (-> sm
    (stack/push-stack arg-start-token)
    (stack/push-flag compile-mode-flag)
    stack/dequeue-code))


(defn import-stdlib-compile-mode [sm]
  (-> sm
      (stack/set-word arg-start-token start-defn)
      (stack/set-mode compile-mode-flag compile-mode)
      (stack/set-mode inner-compile-mode-flag inner-compile-mode)))
  
