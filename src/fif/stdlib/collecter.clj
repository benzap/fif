(ns fif.stdlib.collecter
  "Modes for collecting values, and placing within a data structure

   FIXME: this code is significantly more complex than it should be"
  (:require [fif.stack :as stack]))


(def collecter-mode-flag :collecter-mode)
(def inner-collecter-mode-flag :inner-collecter-mode)
(def generate-collection-mode-flag :generate-collection-mode)

(def arg-start-collecter '<-$)
(def arg-end-collecter '$<-)

(def arg-collect-start '$collecter/start)
(def arg-collect-end '$collecter/end)


(defn generate-collection-mode
  [sm]
  (let [stack (stack/get-stack sm)
        arg (-> sm stack/get-code first)]
    (cond
       (= arg arg-collect-start)
       (-> sm
           (stack/push-stack arg)
           stack/dequeue-code)

       (= arg arg-collect-end)
       (-> sm
           stack/pop-flag
           stack/dequeue-code)
       
       :else
       (let [[new-collection-values new-stack]
             (stack/split-at-token stack arg-collect-start)

             ;; grab our collection, and remove it from the new-stack
             collection (into (peek new-stack) new-collection-values)
             new-stack (pop new-stack)]
         
         ;; TODO: handle maps differently
         (-> sm
             (stack/set-stack (concat [arg-collect-start collection] new-stack))
             stack/process-arg)))))


(defn inner-collecter-mode
  [sm]
  (let [arg (-> stack/get-code first)
        stash (stack/get-stash sm)]
    (cond
       (= arg arg-start-collecter)
       (-> sm
           (stack/push-flag inner-collecter-mode)
           (stack/set-stash (stack/push-sub-stack stash arg))
           stack/dequeue-code)

       (= arg arg-end-collecter)
       (-> sm
           stack/pop-flag
           (stack/set-stash (stack/push-sub-stack stash arg))
           stack/dequeue-code)
  
       :else
       (-> sm
           (stack/set-stash (stack/push-sub-stack stash arg))
           stack/dequeue-code))))


(defn collecter-mode
  [sm]
  (let [arg (-> stack/get-code first)
        stash (stack/get-stash sm)]
    (cond
      (= arg arg-start-collecter)
      (-> sm
          (stack/set-stash (stack/push-sub-stack stash arg))
          (stack/push-flag inner-collecter-mode)
          stack/dequeue-code)

      (= arg arg-end-collecter)
      (let [content (reverse (stack/get-sub-stack stash))
            new-code (concat [arg-collect-start]
                             content
                             [arg-collect-end]
                             (-> sm stack/dequeue-code stack/get-code))]
        (-> sm
            stack/pop-flag
            (stack/set-code new-code)
            (stack/set-stash (stack/remove-sub-stack stash))
            (stack/push-flag generate-collection-mode-flag)
            stack/dequeue-code)))))


(defn start-collecter
  [sm]
  (let [stash (stack/get-stash sm)]
    (-> sm
        (stack/push-flag collecter-mode-flag)
        (stack/set-stash (stack/create-sub-stack stash))
        stack/dequeue-code)))


(defn import-stdlib-collecter-mode
  [sm]
  (-> sm
      (stack/set-mode collecter-mode-flag collecter-mode)
      (stack/set-mode inner-collecter-mode-flag inner-collecter-mode)
      (stack/set-mode generate-collection-mode-flag generate-collection-mode)

      (stack/set-word arg-start-collecter start-collecter)))
