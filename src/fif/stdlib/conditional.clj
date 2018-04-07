(ns fif.stdlib.conditional
  (:require [fif.stack :as stack]))


(def arg-if-token 'if)
(def arg-else-token 'else)
(def arg-then-token 'then)

(def conditional-mode-flag :conditional-mode)


(defn condition-true? [x]
  (cond
   (and (number? x) (= x 0))
   false
   (and (number? x) (not= x 0))
   true
   :else
   (boolean x)))


(defn conditional-mode
  [sm arg]
  (if-not (= arg arg-then-token)
    (stack/push-stack sm arg)
    (let [stack (stack/get-stack sm)

          ;; NOTE: this won't work with nested if conditionals. Need a greedy-take-to-token
          conditional-content
          (reverse (stack/take-to-token stack arg-if-token))

          _ (println "Conditional Content" conditional-content)

          [falsy-content truthy-content]
          (stack/split-at-token conditional-content arg-else-token)

          _ (println "Truthy Falsy" truthy-content falsy-content)

          clean-stack (stack/rest-at-token stack arg-if-token)

          _ (println "Clean Stack" clean-stack)

          bool-flag (-> clean-stack peek condition-true?)

          _ (println "Bool:" bool-flag)
          new-stack (if bool-flag (stack/push-coll (pop clean-stack) truthy-content)
                                  (stack/push-coll (pop clean-stack) falsy-content))]
       (-> sm
         (stack/set-stack new-stack)
         (stack/pop-flag)))))


(conditional-mode (-> (stack/new-stack-machine)
                      (stack/push-flag conditional-mode-flag)
                      (stack/push-stack 0)
                      (stack/push-stack 'if)
                      (stack/push-stack true)
                      (stack/push-stack 'else)
                      (stack/push-stack false))

                  'then)


(defn start-if
  [sm]
  (-> sm
      (stack/push-stack arg-if-token)
      (stack/push-flag conditional-mode-flag)))


(defn import-stdlib-conditional-mode
  [sm]
  (-> sm
      (stack/set-word arg-if-token start-if)
      (stack/set-mode conditional-mode-flag conditional-mode)))
