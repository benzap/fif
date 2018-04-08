(ns fif.compile
  "Defines the compile-mode within the fif stack machine"
  (:require [fif.stack :as stack]))


(def arg-start-token 'fn)
(def arg-end-token 'endfn)
(def compile-mode-flag :compile-mode)


(defn wrap-compiled-fn [wbody]
  (fn [sm]
    (stack/set-code sm (concat wbody (-> sm stack/dequeue-code stack/get-code)))))


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


(compile-mode (-> (stack/new-stack-machine)
                  (stack/push-flag compile-mode-flag)
                  (stack/push-stack 1)
                  (stack/push-stack 'fn)
                  (stack/push-stack 'square)
                  (stack/push-stack 'dup)
                  (stack/push-stack '*)
                  (stack/enqueue-code 'endfn)))


(defn start-defn
  "We retrieved the start-token word, and we push it onto the stack and
   set our compile flag"
  [sm]
  (-> sm
    (stack/push-stack arg-start-token)
    (stack/push-flag compile-mode-flag)
    stack/dequeue-code))



(defn import-compile-mode [sm]
  (-> sm
      (stack/set-word arg-start-token start-defn)
      (stack/set-mode compile-mode-flag compile-mode)))
  
