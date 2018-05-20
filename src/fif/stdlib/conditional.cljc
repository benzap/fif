(ns fif.stdlib.conditional
  "For the if-else-then fif functionality"
  (:require [fif.stack-machine :as stack]
            [fif.stack-machine.sub-stack :as sub-stack]
            [fif.stack-machine.words :as words :refer [set-global-word-defn]]
            [fif.stack-machine.exceptions :as exceptions]
            [fif.stack-machine.processor :as stack.processor]))


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

      ;; Dump any inner conditionals
      (= arg arg-if-token)
      (-> sm
          (stack/push-flag dump-condition-mode-flag)
          stack/dequeue-code)
      
      ;; we've reach the end of our conditional, leave
      ;; dump-condition-mode
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
     
     ;; Throw any additional inner control structures in the dump
     (= arg arg-if-token)
     (-> sm
         (stack/push-flag dump-condition-mode-flag)
         stack/dequeue-code)
     
     ;; We've reached the end of the truth content, and there is false
     ;; content, so pass control onto the false-condition-mode.
     (= arg arg-else-token)
     (-> sm
         stack/pop-flag
         (stack/push-flag false-condition-mode-flag)
         stack/dequeue-code)

     ;; There is no false content, we've reached the end of the
     ;; conditional, So end the mode.
     (= arg arg-then-token)
     (-> sm
         stack/pop-flag
         stack/dequeue-code)
     
     :else
     (-> sm
         stack/dequeue-code))))


(def dump-false-condition-mode dump-condition-mode)


(defn truth-condition-mode
  "Called when the code queue contains truthy content within the code
  queue. This is normally at the beginning of the conditional after
  the 'if clause."
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
      (-> sm stack.processor/process-arg))))


(defn false-condition-mode
  "Called when the code queue includes falsy content. This is normally
  called after the 'else clause."
  [sm]
  (let [arg (-> sm stack/get-code first)]
    (cond
      (= arg arg-then-token)
      (-> sm
          stack/pop-flag
          stack/dequeue-code)

      :else
      (-> sm stack.processor/process-arg))))


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
          (stack/set-stash (sub-stack/push-sub-stack stash arg))
          stack/dequeue-code)

      (= arg arg-else-token)
      (-> sm
          (stack/set-stash (sub-stack/push-sub-stack stash arg))
          stack/dequeue-code)

      (= arg arg-then-token)
      (-> sm
          stack/pop-flag
          (stack/set-stash (sub-stack/push-sub-stack stash arg))
          stack/dequeue-code)

      :else
      (-> sm
          (stack/set-stash (sub-stack/push-sub-stack stash arg))
          stack/dequeue-code))))


(defn conditional-mode
  "Primitive if-else-then statements
  
  Implementation Details:

  - entire control structure is placed in a sub-stack stored on the
  stash. Any inner control structures are processed by
  inner-conditional-mode

  - after being stored, the flag is processed, the stack machine is
  placed in either truth-condition-mode (flag was true) or
  dump-truth-condition-mode (flag was false), and the stashed
  condition body is placed back in the code queue, and the stash is
  cleared.

  - while in truth-condition-mode, args are processed naturally until
  a then statement pops it out of truth-condition-mode

  - while in dump-truth-condition-mode, args are dumped until the
  else (false condition) is reached, which places the stack machine
  in false-condition-mode.  

  "
  [sm]
  (let [arg (-> sm stack/get-code first)
        stash (stack/get-stash sm)]
    (cond

      ;; We found a nested if, we need to process this later
      (= arg arg-if-token)
      (-> sm
          (stack/push-flag inner-conditional-flag)
          (stack/set-stash (sub-stack/push-sub-stack stash arg))
          stack/dequeue-code)
      (= arg arg-then-token)
      (let [[flag] (stack/get-stack sm)
            condition-body (sub-stack/get-sub-stack stash)
            new-code (concat (reverse condition-body)
                             (list arg-then-token)
                             (-> sm stack/dequeue-code stack/get-code))]
        (if (condition-true? flag)
          (-> sm
              stack/pop-stack
              stack/pop-flag
              (stack/push-flag truth-condition-mode-flag)
              (stack/set-code new-code)
              (stack/set-stash (sub-stack/remove-sub-stack stash)))
          ;; Need to dump the truth statement before we pass it to
          ;; false-condition-mode
          (-> sm
              stack/pop-stack
              stack/pop-flag
              (stack/push-flag dump-truth-condition-mode-flag)
              (stack/set-code new-code)
              (stack/set-stash (sub-stack/remove-sub-stack stash)))))

      :else
      (-> sm
          (stack/set-stash (sub-stack/push-sub-stack stash arg))
          stack/dequeue-code))))


(defn start-if
  "Word Definition:
   ? if <truth body> else <false body> then
   ? if <truth body> then"
  [sm]
  (let [stash (stack/get-stash sm)]
    (-> sm
      (stack/set-stash (sub-stack/create-sub-stack stash))
      (stack/push-flag conditional-mode-flag)
      stack/dequeue-code)))

(def doc-string "<flag> if <true-body> [else <false-body>] then -- <flag> determines if <true-body> or <false-body> is executed.")


(defn import-stdlib-conditional-mode
  "Stack Machine imports for if-else-then functionality"
  [sm]
  (-> sm

      (set-global-word-defn
       arg-if-token start-if
       :stdlib? true
       :doc doc-string
       :group :stdlib.mode.conditional)

      (set-global-word-defn
       arg-else-token exceptions/raise-unbounded-mode-argument
       :stdlib? true
       :doc doc-string
       :group :stdlib.mode.conditional)

      (set-global-word-defn
       arg-then-token exceptions/raise-unbounded-mode-argument
       :stdlib? true
       :doc doc-string
       :group :stdlib.mode.conditional)

      (stack/set-mode conditional-mode-flag conditional-mode)
      (stack/set-mode inner-conditional-flag inner-conditional-mode)
      (stack/set-mode truth-condition-mode-flag truth-condition-mode)
      (stack/set-mode dump-condition-mode-flag dump-condition-mode)
      (stack/set-mode dump-truth-condition-mode-flag dump-truth-condition-mode)
      (stack/set-mode dump-false-condition-mode-flag dump-false-condition-mode)
      (stack/set-mode false-condition-mode-flag false-condition-mode)))
