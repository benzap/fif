(ns fif.stdlib.conditional
  "For the if-else-then fif functionality"
  (:require [fif.stack :as stack]))


(def arg-if-token 'if)
(def arg-inner-if-token '!fif--inner-if)

(def arg-else-token 'else)
(def arg-inner-else-token '!fif--inner-else)

(def arg-then-token 'then)
(def arg-inner-then-token '!fif--inner-then)

(def conditional-mode-flag :conditional-mode)
(def inner-conditional-flag :inner-conditional-mode)


(defn condition-true?
  "Determines whether the given value is true or false
  
  Notes:

  - is true if it is a non-zero number
  - is false if it is the number zero
  - is true if (boolean x) is true"
  [x]
  (cond
   (and (number? x) (= x 0))
   false
   (and (number? x) (not= x 0))
   true
   :else
   (boolean x)))


(defn inner-conditional-mode
  "For handling any inner if-else-then tokens, this tracks and converts
  them into a representation which won't impede parsing.

  Notes:

  - additional nested conditionals are tracked by pushing and popping
  additional :inner-conditional-mode flags"
  [sm]
  (let [arg (-> sm stack/get-code first)]
    (cond
      (= arg arg-if-token)
      (-> sm (stack/push-flag inner-conditional-flag)
          (stack/push-stack arg-inner-if-token)
          stack/dequeue-code)
      (= arg arg-else-token)
      (-> sm (stack/push-stack arg-inner-else-token)
          stack/dequeue-code)
      (= arg arg-then-token)
      (-> sm (stack/pop-flag)
          (stack/push-stack arg-inner-then-token)
          stack/dequeue-code)
      :else
      (-> sm (stack/push-stack arg)
          stack/dequeue-code))))
   

(defn clean-inner-conditionals [stack]
  (-> stack
      (stack/replace-token arg-inner-if-token arg-if-token)
      (stack/replace-token arg-inner-then-token arg-then-token)
      (stack/replace-token arg-inner-else-token arg-else-token)))


(defn conditional-mode
  "Primitive if-else-then statements
  
  Implementation Details:

  When the :conditional-mode flag is set, the stack machine will begin
  pulling everything onto the main stack, until we reach the `then`
  statement signifying our conditional content. Any nested
  conditionals trigger the :inner-conditional-mode, which obfuscates
  the conditionals so as not to interfere with the current
  conditional.

  After determining the conditional content, we scrub it from the main
  stack, separate the conditional content into the truthy and falsy
  content, and clean the content from any :inner-conditional-mode
  obfuscation.

  The flag is popped from the stack, and upon determining whether the
  condition and true or false, places either the truthy or falsy
  content back onto the code stack.
  "
  [sm]
  (let [arg (-> sm stack/get-code first)]
    (cond

      ;; We found a nested if, we need to process this later
      (= arg arg-if-token)
      (-> sm (stack/push-flag inner-conditional-flag)
          (stack/push-stack arg-inner-if-token)
          stack/dequeue-code)
      (not= arg arg-then-token)
      (-> sm (stack/push-stack arg) stack/dequeue-code)
      :else
      (let [stack (stack/get-stack sm)

            conditional-content
            (reverse (stack/take-to-token stack arg-if-token))

            [truthy-content falsy-content]
            (stack/split-at-token conditional-content arg-else-token)

            falsy-content (clean-inner-conditionals falsy-content)
            truthy-content (clean-inner-conditionals truthy-content)

            clean-stack (clean-inner-conditionals (stack/rest-at-token stack arg-if-token))

            bool-flag (-> clean-stack peek condition-true?)

            new-code (if bool-flag truthy-content falsy-content)]
        (-> sm
            (stack/set-stack (pop clean-stack))
            (stack/set-code (concat new-code (-> sm stack/dequeue-code stack/get-code)))
            (stack/pop-flag))))))


(defn start-if
  "Word definition for starting an if condition mode"
  [sm]
  (-> sm
      (stack/push-stack arg-if-token)
      (stack/push-flag conditional-mode-flag)
      stack/dequeue-code))


(defn import-stdlib-conditional-mode
  "Stack Machine imports for if-else-then functionality"
  [sm]
  (-> sm
      (stack/set-word arg-if-token start-if)
      (stack/set-mode conditional-mode-flag conditional-mode)
      (stack/set-mode inner-conditional-flag inner-conditional-mode)))
