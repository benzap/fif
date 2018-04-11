(ns fif.stdlib.compile
  "Defines the compile-mode within the fif stack machine"
  (:require [fif.stack :as stack]))


(def arg-start-token 'fn)
(def arg-end-token 'endfn)
(def compile-mode-flag :compile-mode)
(def inner-compile-mode-flag :inner-compile-mode)


(defn wrap-compiled-fn [wbody]
  (fn [sm]
    (stack/set-code sm (vec (concat wbody (-> sm stack/dequeue-code stack/get-code))))))


(defn inner-compile-mode
  [sm]
  (let [arg (-> sm stack/get-code first)
        stash (stack/get-stash sm)]
    (cond
     (= arg arg-start-token)
     (-> sm
         (stack/push-flag inner-compile-mode-flag)
         (stack/set-stash (stack/push-sub-stack stash arg))
         stack/dequeue-code)

     (= arg arg-end-token)
     (-> sm
         stack/pop-flag
         (stack/set-stash (stack/push-sub-stack stash arg))
         stack/dequeue-code)

     :else
     (-> sm
         (stack/set-stash (stack/push-sub-stack stash arg))
         stack/dequeue-code))))


(defn compile-mode
  "This should only be called while we are in compile mode."
  [sm]
  (let [arg (-> sm stack/get-code first)
        stash (stack/get-stash sm)]
    (cond
      (= arg arg-start-token)
      (-> sm
          (stack/set-stash (stack/push-sub-stack stash arg))
          (stack/push-flag inner-compile-mode-flag)
          stack/dequeue-code)

      (= arg arg-end-token)
      (let [fn-content (reverse (stack/get-sub-stack stash))
            [wname & wbody] fn-content]
        (-> sm
            (stack/set-word wname (wrap-compiled-fn wbody))
            (stack/set-stash (stack/remove-sub-stack stash))
            stack/pop-flag
            stack/dequeue-code))

      :else
      (-> sm
          (stack/set-stash (stack/push-sub-stack stash arg))
          stack/dequeue-code))))


(defn start-defn
  "We retrieved the start-token word, and we push it onto the stack and
   set our compile flag"
  [sm]
  (let [stash (stack/get-stash sm)]
    (-> sm
        (stack/push-flag compile-mode-flag)
        (stack/set-stash (stack/create-sub-stack stash))
        stack/dequeue-code)))


(defn import-stdlib-compile-mode [sm]
  (-> sm
      (stack/set-word arg-start-token start-defn)
      (stack/set-mode compile-mode-flag compile-mode)
      (stack/set-mode inner-compile-mode-flag inner-compile-mode)))
  
