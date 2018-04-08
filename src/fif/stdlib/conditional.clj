(ns fif.stdlib.conditional
  (:require [fif.stack :as stack]))


(def arg-if-token 'if)
(def arg-inner-if-token '!fif--inner-if)

(def arg-else-token 'else)
(def arg-inner-else-token '!fif--inner-else)

(def arg-then-token 'then)
(def arg-inner-then-token '!fif--inner-then)

(def conditional-mode-flag :conditional-mode)
(def inner-conditional-flag :inner-conditional-mode)


(defn condition-true? [x]
  (cond
   (and (number? x) (= x 0))
   false
   (and (number? x) (not= x 0))
   true
   :else
   (boolean x)))


(defn inner-conditional-mode
  "For handling any inner if-else-then tokens, this tracks and converts
  them into a representation which won't impede parsing."
  [sm arg]
  (cond
   (= arg arg-if-token)
   (-> sm (stack/push-flag inner-conditional-flag)
          (stack/push-stack arg-inner-if-token))
   (= arg arg-else-token)
   (-> sm (stack/push-stack arg-inner-else-token))
   (= arg arg-then-token)
   (-> sm (stack/pop-flag)
          (stack/push-stack arg-inner-then-token))
   :else
   (stack/push-stack sm arg)))
   

(defn clean-inner-conditionals [stack]
  (-> stack
      (stack/replace-token arg-inner-if-token arg-if-token)
      (stack/replace-token arg-inner-then-token arg-then-token)
      (stack/replace-token arg-inner-else-token arg-else-token)))



(defn conditional-mode
  [sm arg]
  (cond

    ;; We found a nested if, we need to process this later
    (= arg arg-if-token)
    (-> sm (stack/push-flag inner-conditional-flag)
           (stack/push-stack arg-inner-if-token))
    (not= arg arg-then-token)
    (stack/push-stack sm arg)
    :else
    (let [stack (stack/get-stack sm)

          conditional-content
          (reverse (stack/take-to-token stack arg-if-token))

          _ (prn "Content: " conditional-content)

          [falsy-content truthy-content]
          (stack/split-at-token conditional-content arg-else-token)

          _ (prn "Truthy: " truthy-content)
          _ (prn "Falsy: " falsy-content)

          falsy-content (clean-inner-conditionals falsy-content)
          truthy-content (clean-inner-conditionals truthy-content)

          _ (prn "Clean Truthy: " truthy-content)
          _ (prn "Clean Falsy: " falsy-content)

          clean-stack (clean-inner-conditionals (stack/rest-at-token stack arg-if-token))

          _ (prn "Clean Stack:" (pop clean-stack))

          bool-flag (-> clean-stack peek condition-true?)

          _ (prn "Bool Flag: " bool-flag)

          new-code (if bool-flag truthy-content falsy-content)

          _ (prn "New Code: " new-code)]
       (-> sm
         (stack/set-stack (pop clean-stack))
         (stack/set-code (concat ['nop] new-code (-> sm stack/dequeue-code stack/get-code)))
         (stack/pop-flag)))))


(defn start-if
  [sm]
  (-> sm
      (stack/push-stack arg-if-token)
      (stack/push-flag conditional-mode-flag)))


(defn import-stdlib-conditional-mode
  [sm]
  (-> sm
      (stack/set-word arg-if-token start-if)
      (stack/set-mode conditional-mode-flag conditional-mode)
      (stack/set-mode inner-conditional-flag inner-conditional-mode)))
