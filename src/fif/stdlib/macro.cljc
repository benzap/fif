(ns fif.stdlib.macro
  (:require
   [fif.stack-machine :as stack]
   [fif.stack-machine.sub-stack :as sub-stack]
   [fif.stack-machine.words :refer [set-global-word-defn]]
   [fif.stack-machine.exceptions :as exceptions]))


(def macro-define-mode-flag :macro-define-mode)
(def arg-start-macro 'macro)
(def arg-end-macro 'endmacro)


(def macro-store-mode-flag :macro-store-mode)
(def arg-start-macro-store '_!)
(def arg-end-macro-store '!_)


(def arg-create-macro-stack 'macro/stack-create)
(def arg-transfer-macro-stack 'macro/stack->code-stack)


(defn wrap-compiled-macro [wbody]
  (fn [sm]
    (stack/set-code sm
      (concat
       [arg-create-macro-stack]
       wbody
       [arg-transfer-macro-stack]
       (-> sm stack/dequeue-code stack/get-code)))))
       


(defn macro-define-mode
  [sm]
  (let [arg (-> sm stack/get-code first)
        stash (stack/get-stash sm)]
    (cond
      (= arg arg-end-macro)
      (let [macro-content (reverse (sub-stack/get-sub-stack stash))
            [wname & wbody] macro-content]
        (-> sm
            (stack/set-word wname (wrap-compiled-macro wbody))
            (stack/set-stash (sub-stack/remove-sub-stack stash))
            stack/pop-flag
            stack/dequeue-code))
      :else
      (-> sm
          (stack/set-stash (sub-stack/push-sub-stack stash arg))
          stack/dequeue-code))))


(defn start-macro
  [sm]
  (let [stash (stack/get-stash sm)]
    (-> sm
        (stack/push-flag macro-define-mode-flag)
        (stack/set-stash (sub-stack/create-sub-stack stash))
        stack/dequeue-code)))


(defn macro-store-mode
  [sm]
  (let [arg (-> sm stack/get-code first)
        temp (stack/get-temp-macro sm)]
    (cond
       (= arg arg-end-macro-store)
       (-> sm
           stack/pop-flag
           stack/dequeue-code)
       :else
       (-> sm
           (stack/set-temp-macro (sub-stack/push-sub-stack temp arg))
           stack/dequeue-code))))
    

(defn op-create-temp-macro-stack
  [sm]
  (let [temp (stack/get-temp-macro sm)]
    (-> sm
        (stack/set-temp-macro (sub-stack/create-sub-stack temp))
        stack/dequeue-code)))


(defn op-transfer-macro-stack
  [sm]
  (let [temp (stack/get-temp-macro sm)
        macro-content (-> sm stack/get-temp-macro peek reverse)]
    (-> sm
        (stack/set-code (concat macro-content (-> sm stack/dequeue-code stack/get-code)))
        (stack/set-temp-macro (sub-stack/remove-sub-stack temp)))))


(defn start-macro-store
  [sm]
  (-> sm
      (stack/push-flag macro-store-mode-flag)
      stack/dequeue-code))


(defn import-stdlib-macro-mode
  [sm]
  (-> sm

      (set-global-word-defn arg-start-macro start-macro :stdlib? true)

      (set-global-word-defn arg-start-macro-store start-macro-store :stdlib? true)

      (set-global-word-defn arg-create-macro-stack op-create-temp-macro-stack :stdlib? true)

      (set-global-word-defn arg-transfer-macro-stack op-transfer-macro-stack :stdlib? true)

      (stack/set-mode macro-define-mode-flag macro-define-mode)
      (stack/set-mode macro-store-mode-flag macro-store-mode)))
