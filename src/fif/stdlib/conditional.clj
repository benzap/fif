(ns fif.stdlib.conditional
  "For the if-else-then fif functionality"
  (:require [fif.stack :as stack]))


(def arg-if-token 'if)
(def arg-else-token 'else)
(def arg-then-token 'then)

(def conditional-mode-flag :conditional-mode)
(def inner-conditional-flag :inner-conditional-mode)
(def truth-condition-mode-flag :truth-condition-mode)
(def dump-truth-condition-mode-flag :dump-truth-condition-mode)
(def dump-false-condition-mode-flag :dump-false-condition-mode)
(def dump-condition-mode-flag :dump-condition-mode)
(def false-condition-mode-flag :false-condition-mode)



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


(defn dump-condition-mode
  "Dumps the code stack, but stops upon reaching the end of the
  condition body (then)."
  [sm]
  (let [arg (-> sm stack/get-code first)]
    (cond
      (= arg arg-if-token)
      (-> sm
          (stack/push-flag dump-condition-mode-flag)
          stack/dequeue-code)
      
      (= arg arg-then-token)
      (-> sm
          stack/pop-flag
          stack/dequeue-code)
      
      :else
      (-> sm stack/dequeue-code))))


(defn dump-truth-condition-mode
  "Dumps the code stack, but stops upon reaching an else condition or
  upon reaching the end of the condition body (then). Nested
  conditionals are passed to dump-condition-mode.

  Notes:

  - This mode is assumed to be a truth content dump, in order to process the
  false conditional body"
  [sm]
  (let [arg (-> sm stack/get-code first)]
    (cond
     
     (= arg arg-if-token)
     (-> sm
         (stack/push-flag dump-condition-mode-flag)
         stack/dequeue-code)
     
     (= arg arg-else-token)
     (-> sm
         stack/pop-flag
         (stack/push-flag false-condition-mode-flag)
         stack/dequeue-code)

     (= arg arg-then-token)
     (-> sm
         stack/pop-flag
         stack/dequeue-code)
     
     :else
     (-> sm
         stack/dequeue-code))))


(def dump-false-condition-mode dump-condition-mode)


(defn truth-condition-mode
  [sm]
  (let [arg (-> sm stack/get-code first)]
    (cond
      (= arg arg-else-token)
      (-> sm
          stack/pop-flag
          (stack/push-flag dump-false-condition-mode-flag)
          stack/dequeue-code)

      (= arg arg-then-token)
      (-> sm
          stack/pop-flag
          stack/dequeue-code)
 
      :else
      (-> sm stack/process-arg))))


(defn false-condition-mode
  [sm]
  (let [arg (-> sm stack/get-code first)]
    (cond
      (= arg arg-then-token)
      (-> sm
          stack/pop-flag
          stack/dequeue-code)

      :else
      (-> sm stack/process-arg))))


(defn inner-conditional-mode
  "For handling any inner if-else-then tokens, this tracks and converts
  them into a representation which won't impede parsing.

  Notes:

  - additional nested conditionals are tracked by pushing and popping
  additional :inner-conditional-mode flags"
  [sm]
  (let [arg (-> sm stack/get-code first)
        stash (stack/get-stash sm)]
    (cond

      (= arg arg-if-token)
      (-> sm
          (stack/push-flag inner-conditional-flag)
          (stack/set-stash (stack/push-sub-stack stash arg))
          stack/dequeue-code)

      (= arg arg-else-token)
      (-> sm
          (stack/set-stash (stack/push-sub-stack stash arg))
          stack/dequeue-code)

      (= arg arg-then-token)
      (-> sm
          stack/pop-flag
          (stack/set-stash (stack/push-sub-stack stash arg))
          stack/dequeue-code)

      :else
      (-> sm
          (stack/set-stash (stack/push-sub-stack stash arg))
          stack/dequeue-code))))


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
  (let [arg (-> sm stack/get-code first)
        stash (stack/get-stash sm)]
    (cond

      ;; We found a nested if, we need to process this later
      (= arg arg-if-token)
      (-> sm
          (stack/push-flag inner-conditional-flag)
          (stack/set-stash (stack/push-sub-stack stash arg))
          stack/dequeue-code)
      (= arg arg-then-token)
      (let [[flag] (stack/get-stack sm)
            condition-body (stack/get-sub-stack stash)
            new-code (concat (reverse condition-body)
                             (list arg-then-token)
                             (-> sm stack/dequeue-code stack/get-code))]
        (if (condition-true? flag)
          (-> sm
              stack/pop-stack
              stack/pop-flag
              (stack/push-flag truth-condition-mode-flag)
              (stack/set-code new-code)
              (stack/set-stash (stack/remove-sub-stack stash)))
          ;; Need to dump the truth statement before we pass it to
          ;; false-condition-mode
          (-> sm
              stack/pop-stack
              stack/pop-flag
              (stack/push-flag dump-truth-condition-mode-flag)
              (stack/set-code new-code)
              (stack/set-stash (stack/remove-sub-stack stash)))))

      :else
      (-> sm
          (stack/set-stash (stack/push-sub-stack stash arg))
          stack/dequeue-code))))


(defn start-if
  "Word definition for starting an if condition mode"
  [sm]
  (let [stash (stack/get-stash sm)]
    (-> sm
      (stack/set-stash (stack/create-sub-stack stash))
      (stack/push-flag conditional-mode-flag)
      stack/dequeue-code)))


(defn import-stdlib-conditional-mode
  "Stack Machine imports for if-else-then functionality"
  [sm]
  (-> sm
      (stack/set-word arg-if-token start-if)
      (stack/set-mode conditional-mode-flag conditional-mode)
      (stack/set-mode inner-conditional-flag inner-conditional-mode)
      (stack/set-mode truth-condition-mode-flag truth-condition-mode)
      (stack/set-mode dump-condition-mode-flag dump-condition-mode)
      (stack/set-mode dump-truth-condition-mode-flag dump-truth-condition-mode)
      (stack/set-mode dump-false-condition-mode-flag dump-false-condition-mode)
      (stack/set-mode false-condition-mode-flag false-condition-mode)))
