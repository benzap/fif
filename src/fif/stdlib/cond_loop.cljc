(ns fif.stdlib.cond-loop
  (:require
    [fif.stack-machine :as stack]
    [fif.stack-machine.sub-stack :as sub-stack]
    [fif.stack-machine.words :refer [set-global-word-defn]]
    [fif.stack-machine.exceptions :as exceptions]
    [fif.utils.token :as token]
    [fif.stdlib.conditional :refer [condition-true?]]
    [fif.stack-machine.processor :as stack.processor]))


(def arg-do-token 'do)
(def arg-loop-token 'loop)
(def arg-loopend-token 'loopend)
(def arg-plus-loop-token '+loop)
(def arg-plus-loopend-token '+loopend)
(def arg-leave-token 'leave)

;; end start do ... loop
;; end start do ... +loop

(def arg-begin-token 'begin)
(def arg-until-token 'until)
(def arg-untilend-token 'untilend)

;; begin ... ? until

(def arg-while-token 'while)
(def arg-repeat-token 'repeat)
(def arg-repeatend-token 'repeatend)

;; begin ? while ... repeat

(def do-mode-flag :do-mode)
(def inner-do-mode-flag :inner-do-mode)
(def loop-mode-flag :loop-mode)
(def loop-leave-mode-flag :loop-leave-mode)
(def loop-plus-mode-flag :loop-plus-mode)

(def begin-mode-flag :begin-mode)
(def inner-begin-mode-flag :inner-begin-mode)
(def begin-until-mode-flag :begin-until-mode)
(def begin-until-leave-mode-flag :begin-until-leave-mode)
(def begin-while-mode-flag :begin-while-mode)
(def begin-while-leave-mode-flag :begin-while-leave-mode)
(def begin-dump-mode-flag :begin-dump-mode)


(defn get-loop-start-index [sm]
  (-> sm stack/get-ret peek first))


(defn get-loop-end-index [sm]
  (-> sm stack/get-ret peek second))


(defn increment-loop-index [sm i]
  (let [[start end] (-> sm stack/get-ret peek)]
    (-> sm stack/pop-ret (stack/push-ret [(+ start i) end]))))


(defn loop-leave-mode
  [sm]
  (let [arg (-> sm stack/get-code first)
        stash (stack/get-stash sm)]
    (cond
      (or (= arg arg-loopend-token)
          (= arg arg-plus-loopend-token))
      (-> sm
          stack/pop-flag
          (stack/set-stash (sub-stack/remove-sub-stack stash))
          stack/pop-ret
          stack/dequeue-code)
      :else
      (-> sm stack/dequeue-code))))


(defn loop-mode
  [sm]
  (let [arg (-> sm stack/get-code first)
        stash (stack/get-stash sm)]
    (cond
      ;; Finished processing the current loop iteration
      (= arg arg-loopend-token)
      (let [start-idx (get-loop-start-index sm)
            end-idx (get-loop-end-index sm)]
        (if-not (>= start-idx end-idx)

          ;; Prepare the next loop iteration
          (let [loop-body (sub-stack/get-sub-stack stash)
                new-code (concat (reverse loop-body)
                                 (list arg-loopend-token)
                                 (-> sm stack/dequeue-code stack/get-code))]
            (-> sm
                (increment-loop-index 1)
                (stack/set-code new-code)))

          ;; Clean up the return stack, remove our loop content from
          ;; the stash, get out of loop mode and continue execution
          (-> sm
              stack/pop-flag
              (stack/set-stash (sub-stack/remove-sub-stack stash))
              stack/pop-ret
              stack/dequeue-code)))

      ;; Finished processing the current loop iteration
      (= arg arg-plus-loopend-token)
      (let [start-idx (get-loop-start-index sm)
            end-idx (get-loop-end-index sm)]
        (if-not (>= start-idx end-idx)

          ;; Prepare the next loop iteration
          (let [[loop-step] (stack/get-stack sm)
                loop-body (sub-stack/get-sub-stack stash)
                new-code (concat (reverse loop-body)
                                 (list arg-plus-loopend-token)
                                 (-> sm stack/dequeue-code stack/get-code))]
            (-> sm
                stack/pop-stack
                (increment-loop-index loop-step)
                (stack/set-code new-code)))

          ;; Clean up the return stack, remove our loop content from
          ;; the stash, get out of loop mode and continue execution
          (-> sm
              stack/pop-stack
              stack/pop-flag
              (stack/set-stash (sub-stack/remove-sub-stack stash))
              stack/pop-ret
              stack/dequeue-code)))
      
      :else
      (-> sm stack.processor/process-arg))))


(defn inner-do-mode
  [sm]
  (let [arg (-> sm stack/get-code first)
        stash (stack/get-stash sm)]
    (cond
      (or (= arg arg-loop-token)
          (= arg arg-plus-loop-token))
      (-> sm
          stack/pop-flag
          (stack/set-stash (sub-stack/push-sub-stack stash arg))
          stack/dequeue-code)
       
      (= arg arg-do-token)
      (-> sm
          (stack/push-flag inner-do-mode-flag)
          (stack/set-stash (sub-stack/push-sub-stack stash arg))
          stack/dequeue-code)

      :else
      (-> sm
          (stack/set-stash (sub-stack/push-sub-stack stash arg))
          stack/dequeue-code))))


(defn do-mode
  [sm]
  (let [arg (-> sm stack/get-code first)
        stash (stack/get-stash sm)]
    (cond
      (= arg arg-do-token)
      (-> sm
          (stack/push-flag inner-do-mode-flag)
          (stack/set-stash (sub-stack/push-sub-stack stash arg))
          stack/dequeue-code)

      ;; Go into loop-mode
      (or (= arg arg-loop-token)
          (= arg arg-plus-loop-token))
      (let [loop-body (sub-stack/get-sub-stack stash)
            end-token (cond (= arg arg-loop-token)
                            arg-loopend-token
                            (= arg arg-plus-loop-token)
                            arg-plus-loopend-token)
            new-code (concat (reverse loop-body)
                             (list end-token)
                             (-> sm stack/dequeue-code stack/get-code))]
        (-> sm
            
            ;; Leave do-mode and enter loop-mode
            stack/pop-flag
            (stack/push-flag loop-mode-flag)

            ;; The stashed away loop content can now be placed back on
            ;; the code queue for the first loop iteration
            (stack/set-code new-code)))

      ;; Push the loop body into the stash sub-stack
      :else
      (-> sm
          (stack/set-stash (sub-stack/push-sub-stack stash arg))
          stack/dequeue-code))))


(defn start-do
  "Do structure is <start> <end> do (<body> loop)|(<body ... step> +loop)"
  [sm]
  (let [[start end] (-> sm stack/get-stack)
        stash (stack/get-stash sm)]
    (-> sm

        ;; pop the start and end values off of the stack
        stack/pop-stack
        stack/pop-stack

        ;; push the start and end values onto the return stack
        (stack/push-ret [start end])

        ;; start do-mode
        (stack/push-flag do-mode-flag)

        ;; create and stash a sub-stack to hold our loop body
        (stack/set-stash (sub-stack/create-sub-stack stash))

        ;; remove code token for next iteration
        stack/dequeue-code)))


(defn get-loop-index-1
  [sm]
  (let [idx (-> sm stack/get-ret first first)]
    (-> sm (stack/push-stack idx) stack/dequeue-code)))


(defn get-loop-index-2
  [sm]
  (let [idx (-> sm stack/get-ret second first)]
    (-> sm (stack/push-stack idx) stack/dequeue-code)))


(defn get-loop-index-3
  [sm]
  (let [idx (-> sm stack/get-ret (nth 2) first)]
    (-> sm (stack/push-stack idx) stack/dequeue-code)))


(defn begin-until-leave-mode
  [sm]
  (let [arg (-> sm stack/get-code first)
        stash (stack/get-stash sm)]
    (cond
      (= arg arg-untilend-token)
      (-> sm
          stack/pop-flag
          (stack/set-stash (sub-stack/remove-sub-stack stash))
          stack/dequeue-code)
      :else
      (-> sm stack/dequeue-code))))


(defn begin-while-leave-mode
  [sm]
  (let [arg (-> sm stack/get-code first)
        stash (stack/get-stash sm)]
    (cond
      (= arg arg-repeatend-token)
      (-> sm
          stack/pop-flag
          (stack/set-stash (sub-stack/remove-sub-stack stash))
          stack/dequeue-code)
      :else
      (-> sm stack/dequeue-code))))


(defn begin-dump-mode
  [sm]
  (let [arg (-> sm stack/get-code first)]
    (cond
      (= arg arg-begin-token)
      (-> sm
          (stack/push-flag begin-dump-mode-flag)
          stack/dequeue-code)

      (or (= arg arg-until-token)
          (= arg arg-repeat-token)
          (= arg arg-repeatend-token))
      (-> sm 
          stack/pop-flag
          stack/dequeue-code)

      :else
      (-> sm stack/dequeue-code))))


(defn begin-while-mode
  [sm]
  (let [arg (-> sm stack/get-code first)
        stash (stack/get-stash sm)]
    (cond

      ;; Determine if the rest of the loop content should be
      ;; processed.  If it can be processed, continue on until we
      ;; reach the repeat clause If we are not continuing, then we
      ;; need to dump the loop content and continue.
      (= arg arg-while-token)
      (let [[flag] (-> sm stack/get-stack)]
        (if (condition-true? flag)
          (-> sm
              stack/pop-stack
              stack/dequeue-code)
          (-> sm
              stack/pop-stack
              stack/pop-flag
              (stack/push-flag begin-dump-mode-flag)
              (stack/set-stash (sub-stack/remove-sub-stack stash))
              stack/dequeue-code)))

      ;; Finished processing the current loop iteration
      (= arg arg-repeatend-token)
     
      ;; Prepare the next loop iteration
      (let [loop-body (sub-stack/get-sub-stack stash)
            new-code (concat (reverse loop-body)
                             (list arg-repeatend-token)
                             (-> sm stack/dequeue-code stack/get-code))]
        (-> sm
            (stack/set-code new-code)))
      
      :else
      (-> sm stack.processor/process-arg))))

(defn begin-until-mode
  [sm]
  (let [arg (-> sm stack/get-code first)
        stash (stack/get-stash sm)]
    (cond

      ;; Finished processing the current loop iteration
      (= arg arg-untilend-token)
      (let [[flag] (-> sm stack/get-stack)]
        (if-not (condition-true? flag)
          
          ;; Prepare the next loop iteration
          (let [loop-body (sub-stack/get-sub-stack stash)
                new-code (concat (reverse loop-body)
                                 (list arg-untilend-token)
                                 (-> sm stack/dequeue-code stack/get-code))]
            (-> sm
                stack/pop-stack
                (stack/set-code new-code)))

          ;; Clean up the return stack, remove our loop content from
          ;; the stash, get out of begin-until-mode and continue execution
          (-> sm
              stack/pop-stack
              stack/pop-flag
              (stack/set-stash (sub-stack/remove-sub-stack stash))
              stack/dequeue-code)))

      :else
      (-> sm stack.processor/process-arg))))


(defn inner-begin-mode
  [sm]
  (let [arg (-> sm stack/get-code first)
        stash (stack/get-stash sm)]
    (cond
      (or (= arg arg-until-token)
          (= arg arg-repeat-token))
      (-> sm
          stack/pop-flag
          (stack/set-stash (sub-stack/push-sub-stack stash arg))
          stack/dequeue-code)
       
      (= arg arg-begin-token)
      (-> sm
          (stack/push-flag inner-do-mode-flag)
          (stack/set-stash (sub-stack/push-sub-stack stash arg))
          stack/dequeue-code)

      :else
      (-> sm
          (stack/set-stash (sub-stack/push-sub-stack stash arg))
          stack/dequeue-code))))

(defn begin-mode
  [sm]
  (let [arg (-> sm stack/get-code first)
        stash (stack/get-stash sm)]
    (cond
      (= arg arg-begin-token)
      (-> sm
          (stack/push-flag inner-begin-mode-flag)
          (stack/set-stash (sub-stack/push-sub-stack stash arg))
          stack/dequeue-code)

      ;; Go into begin-until-mode
      (= arg arg-until-token)
      (let [loop-body (sub-stack/get-sub-stack stash)
            new-code (concat (reverse loop-body)
                             (list arg-untilend-token)
                             (-> sm stack/dequeue-code stack/get-code))]
        (-> sm
            
            ;; Leave begin-mode and enter begin-until-mode
            stack/pop-flag
            (stack/push-flag begin-until-mode-flag)

            ;; The stashed away loop content can now be placed back on
            ;; the code queue for the first loop iteration
            (stack/set-code new-code)))


      ;; Go into begin-until-mode
      (= arg arg-repeat-token)
      (let [loop-body (sub-stack/get-sub-stack stash)
            new-code (concat (reverse loop-body)
                             (list arg-repeatend-token)
                             (-> sm stack/dequeue-code stack/get-code))]
        (-> sm
            
            ;; Leave begin-mode and enter begin-while-mode
            stack/pop-flag
            (stack/push-flag begin-while-mode-flag)

            ;; The stashed away loop content can now be placed back on
            ;; the code queue for the first loop iteration
            (stack/set-code new-code)))


      ;; Push the loop body into the stash sub-stack
      :else
      (-> sm
          (stack/set-stash (sub-stack/push-sub-stack stash arg))
          stack/dequeue-code))))


(defn start-begin
  "Begin structure begin <body ... ?> until
                   begin <?> while <body> repeat"
  [sm]
  (let [[start end] (-> sm stack/get-stack)
        stash (stack/get-stash sm)]
    (-> sm

        ;; start begin-mode
        (stack/push-flag begin-mode-flag)

        ;; create and stash a sub-stack to hold our loop body
        (stack/set-stash (sub-stack/create-sub-stack stash))

        ;; remove code token for next iteration
        stack/dequeue-code)))


(defn leave-recent-loop
  "Find the most recent loop flag and replace it with a loop leave
  mode.

  Notes:

  - Note that any additional conditional modes that are passed need to
  be also be replaced with a dump to preserve the stash."
  [sm]
  (let [flags (-> sm stack/get-flags reverse)

        ;;FIXME: does not handle situations where there is no loop flags.
        recent-begin-until (concat (token/take-to-token flags begin-until-mode-flag)
                                   [begin-until-mode-flag])
        recent-begin-while (concat (token/take-to-token flags begin-while-mode-flag)
                                   [begin-while-mode-flag])
        recent-loop (concat (token/take-to-token flags loop-mode-flag)
                            [loop-mode-flag])
        
        recent-listing
        (->> (sort-by count [recent-begin-until recent-begin-while recent-loop])
             first)

        leave-loop-tag
        (condp = (last recent-listing)
          loop-mode-flag loop-leave-mode-flag
          begin-until-mode-flag begin-until-leave-mode-flag
          begin-while-mode-flag begin-while-leave-mode-flag)
        
        new-flags 
        (as-> flags $
          (drop (count recent-listing) $)
          (reverse $)
          (concat $ [leave-loop-tag])
          (vec $))]
    (-> sm (stack/set-flags new-flags))))


#_(-> (stack/new-stack-machine)
      (stack/push-flag :test)
      (stack/push-flag :another-value)
      (stack/push-flag begin-until-mode-flag)
      (stack/push-flag begin-while-mode-flag)
      (stack/push-flag :test2)
      (stack/push-flag :test3)
      (stack/push-flag loop-mode-flag)
      (leave-recent-loop)
      (stack/get-flags))


(defn start-leave
  "Leaves whatever the current loop is.
   
   Goes through the flags, and replaces the most recent loop with a dump-mode"
  [sm]
  (let []
    (-> sm leave-recent-loop stack/dequeue-code)))


(def doc-string-do "<end> <start> do <body> loop|+loop -- Loop conditional")
(def doc-string-begin "begin <body> <flag> until | begin <flag> while <body> repeat -- Loop conditional -- Loop conditional")


(defn import-stdlib-cond-loop-mode [sm]
  (-> sm

      (set-global-word-defn
       arg-do-token start-do
       :stdlib? true
       :doc doc-string-do
       :group :stdlib.mode.conditional-loop)

      (set-global-word-defn
       arg-loop-token exceptions/raise-unbounded-mode-argument
       :stdlib? true
       :doc doc-string-do
       :group :stdlib.mode.conditional-loop)

      (set-global-word-defn
       arg-loopend-token exceptions/raise-unbounded-mode-argument
       :stdlib? true
       :doc doc-string-do
       :group :stdlib.mode.conditional-loop)

      (set-global-word-defn
       arg-plus-loop-token exceptions/raise-unbounded-mode-argument
       :stdlib? true
       :doc doc-string-do
       :group :stdlib.mode.conditional-loop)

      (set-global-word-defn
       arg-plus-loopend-token exceptions/raise-unbounded-mode-argument
       :stdlib? true
       :doc doc-string-do
       :group :stdlib.mode.conditional-loop)

      (set-global-word-defn
       'i get-loop-index-1
       :stdlib? true
       :doc "( -- n ) Retrieve the index of the innermost loop."
       :group :stdlib.mode.conditional-loop)

      (set-global-word-defn
       'j get-loop-index-2
       :stdlib? true
       :doc "( -- n ) Retrieve the index of the second from the innermost loop.")

      (set-global-word-defn
       'k get-loop-index-3
       :stdlib? true
       :doc "( -- n ) Retrieve the index of the third from the innermost loop.")

      (stack/set-mode do-mode-flag do-mode)
      (stack/set-mode inner-do-mode-flag inner-do-mode)
      (stack/set-mode loop-mode-flag loop-mode)
      (stack/set-mode loop-leave-mode-flag loop-leave-mode)

      (set-global-word-defn
       arg-begin-token start-begin
       :stdlib? true
       :doc doc-string-begin
       :group :stdlib.mode.conditional-loop)

      (set-global-word-defn
       arg-until-token exceptions/raise-unbounded-mode-argument
       :stdlib? true
       :doc doc-string-begin
       :group :stdlib.mode.conditional-loop)

      (set-global-word-defn
       arg-untilend-token exceptions/raise-unbounded-mode-argument
       :stdlib? true
       :doc doc-string-begin
       :group :stdlib.mode.conditional-loop)

      (set-global-word-defn
       arg-while-token exceptions/raise-unbounded-mode-argument
       :stdlib? true
       :doc doc-string-begin
       :group :stdlib.mode.conditional-loop)

      (set-global-word-defn
       arg-repeat-token exceptions/raise-unbounded-mode-argument
       :stdlib? true
       :doc doc-string-begin
       :group :stdlib.mode.conditional-loop)

      (set-global-word-defn
       arg-repeatend-token exceptions/raise-unbounded-mode-argument
       :stdlib? true
       :doc doc-string-begin
       :group :stdlib.mode.conditional-loop)

      (stack/set-mode begin-mode-flag begin-mode)
      (stack/set-mode inner-begin-mode-flag inner-begin-mode)
      (stack/set-mode begin-until-mode-flag begin-until-mode)
      (stack/set-mode begin-while-mode-flag begin-while-mode)
      (stack/set-mode begin-dump-mode-flag begin-dump-mode)
      (stack/set-mode begin-while-leave-mode-flag begin-while-leave-mode)
      (stack/set-mode begin-until-leave-mode-flag begin-until-leave-mode)

      (set-global-word-defn
       arg-leave-token start-leave
       :stdlib? true
       :doc "Used within a conditional loop to leave early"
       :group :stdlib.mode.conditional-loop)))
