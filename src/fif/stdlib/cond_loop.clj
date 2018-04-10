(ns fif.stdlib.cond-loop
  (:require
    [fif.stack :as stack]
    [fif.stdlib.conditional :refer [condition-true?]]))


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
(def loop-plus-mode-flag :loop-plus-mode)

(def begin-mode-flag :begin-mode)
(def inner-begin-mode-flag :inner-begin-mode)
(def begin-until-mode-flag :begin-until-mode)
(def begin-while-mode-flag :begin-while-mode)


(defn get-loop-start-index [sm]
  (-> sm stack/get-ret peek first))


(defn get-loop-end-index [sm]
  (-> sm stack/get-ret peek second))


(defn increment-loop-index [sm i]
  (let [[start end] (-> sm stack/get-ret peek)]
    (-> sm stack/pop-ret (stack/push-ret [(+ start i) end]))))


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
         (let [loop-body (stack/get-sub-stack stash)
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
             (stack/set-stash (stack/remove-sub-stack stash))
             stack/pop-ret
             stack/dequeue-code)))

     ;; Finished processing the current loop iteration
     (= arg arg-plus-loopend-token)
     (let [start-idx (get-loop-start-index sm)
           end-idx (get-loop-end-index sm)]
       (if-not (>= start-idx end-idx)

         ;; Prepare the next loop iteration
         (let [[loop-step] (stack/get-stack sm)
               loop-body (stack/get-sub-stack stash)
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
             (stack/set-stash (stack/remove-sub-stack stash))
             stack/pop-ret
             stack/dequeue-code)))
               
     :else
     (-> sm stack/process-arg stack/inc-step))))


(defn inner-do-mode
  [sm]
  (let [arg (-> sm stack/get-code first)
        stash (stack/get-stash sm)]
    (cond
      (or (= arg arg-loop-token)
          (= arg arg-plus-loop-token))
      (-> sm
          stack/pop-flag
          (stack/set-stash (stack/push-sub-stack stash arg))
          stack/dequeue-code)
       
      (= arg arg-do-token)
      (-> sm
          (stack/push-flag inner-do-mode-flag)
          (stack/set-stash (stack/push-sub-stack stash arg))
          stack/dequeue-code)

      :else
      (-> sm
          (stack/set-stash (stack/push-sub-stack stash arg))
          stack/dequeue-code))))


(defn do-mode
  [sm]
  (let [arg (-> sm stack/get-code first)
        stash (stack/get-stash sm)]
    (cond
      (= arg arg-do-token)
      (-> sm
          (stack/push-flag inner-do-mode-flag)
          (stack/set-stash (stack/push-sub-stack stash arg))
          stack/dequeue-code)

      ;; Go into loop-mode
      (or (= arg arg-loop-token)
          (= arg arg-plus-loop-token))
      (let [loop-body (stack/get-sub-stack stash)
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
          (stack/set-stash (stack/push-sub-stack stash arg))
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
        (stack/set-stash (stack/create-sub-stack stash))

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


(defn begin-while-mode
  [sm])

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
          (let [loop-body (stack/get-sub-stack stash)
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
              (stack/set-stash (stack/remove-sub-stack stash))
              stack/dequeue-code)))
      
      :else
      (-> sm stack/process-arg stack/inc-step))))


(defn inner-begin-mode
  [sm]
  (let [arg (-> sm stack/get-code first)
        stash (stack/get-stash sm)]
    (cond
      (or (= arg arg-until-token)
          (= arg arg-repeat-token))
      (-> sm
          stack/pop-flag
          (stack/set-stash (stack/push-sub-stack stash arg))
          stack/dequeue-code)
       
      (= arg arg-begin-token)
      (-> sm
          (stack/push-flag inner-do-mode-flag)
          (stack/set-stash (stack/push-sub-stack stash arg))
          stack/dequeue-code)

      :else
      (-> sm
          (stack/set-stash (stack/push-sub-stack stash arg))
          stack/dequeue-code))))

(defn begin-mode
  [sm]
  (let [arg (-> sm stack/get-code first)
        stash (stack/get-stash sm)]
    (cond
      (= arg arg-begin-token)
      (-> sm
          (stack/push-flag inner-begin-mode-flag)
          (stack/set-stash (stack/push-sub-stack stash arg))
          stack/dequeue-code)

      ;; Go into begin-until-mode
      (= arg arg-until-token)
      (let [loop-body (stack/get-sub-stack stash)
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
      (let [loop-body (stack/get-sub-stack stash)
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
          (stack/set-stash (stack/push-sub-stack stash arg))
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
        (stack/set-stash (stack/create-sub-stack stash))

        ;; remove code token for next iteration
        stack/dequeue-code)))


(defn import-stdlib-cond-loop-mode [sm]
  (-> sm
      (stack/set-word arg-do-token start-do)
      (stack/set-word 'i get-loop-index-1)
      (stack/set-word 'j get-loop-index-2)
      (stack/set-word 'k get-loop-index-3)
      (stack/set-mode do-mode-flag do-mode)
      (stack/set-mode inner-do-mode-flag inner-do-mode)
      (stack/set-mode loop-mode-flag loop-mode)

      (stack/set-word arg-begin-token start-begin)
      (stack/set-mode begin-mode-flag begin-mode)
      (stack/set-mode inner-begin-mode-flag inner-begin-mode)
      (stack/set-mode begin-until-mode-flag begin-until-mode)
      (stack/set-mode begin-while-mode-flag begin-while-mode)))
      
