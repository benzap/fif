(ns fif.stack-machine
  "Main Stack Machine Protocol which stores state machine related
  information, and stores the entirety of the language functionality.

  Creating the stack machine with the standard libraries is what makes
  up fif.")


(defprotocol IStackMachine
  (push-stack* [this arg])
  (pop-stack [this])
  (get-stack [this])
  (set-stack [this stack])
  (clear-stack [this])

  (push-ret [this ret])
  (pop-ret [this])
  (get-ret [this])
  (clear-ret [this])

  ;; Old Functionality
  (get-stash [this])
  (set-stash [this st])
  (clear-stash [this])

  ;; Will Replace Old Functionality
  (get-mode-stash [this])
  (set-mode-stash [this stash])
  (clear-mode-stash [this])

  (get-scope [this])
  (set-scope [this scope])
  (clear-scope [this])

  ;; Soon-to-be Deprecated
  (push-temp-macro [this x])
  (pop-temp-macro [this])
  (get-temp-macro [this])
  (set-temp-macro [this st])
  (pick-temp-macro [this])
  (clear-temp-macro [this])

  (set-word* [this wname wbody])
  (get-word [this wname])
  (get-word-listing [this])
  (set-variable [this wname value])

  (set-word-metadata [this wname wmeta])
  (get-word-metadata [this wname])

  (set-mode [this flag modefn])
  (remove-mode [this flag])

  (push-flag [this flag])
  (pop-flag [this])
  (get-flags [this])
  (set-flags [this flags])
  (clear-flags [this])

  (push-code [this arg])
  (enqueue-code [this arg])
  (dequeue-code [this])
  (set-code [this stack])
  (get-code [this])
  (clear-code [this])

  (get-step-max [this])
  (set-step-max [this m])
  (inc-step [this])
  (set-step-num [this n])
  (get-step-num [this])

  (enable-debug [this])
  (disable-debug [this])
  (is-debug-mode? [this])

  (get-system-error-handler [this])
  (set-system-error-handler [this err-handler])

  (get-stack-error-handler [this])
  (set-stack-error-handler [this err-handler])

  (step [this])
  (run [this])
  (halt [this])
  (continue [this]))


(defn push-stack
  ([sm x] (push-stack* sm x))
  ([sm x1 x2]
   (-> sm
       (push-stack* x1)
       (push-stack* x2)))
  ([sm x1 x2 x3]
   (-> sm
       (push-stack* x1)
       (push-stack* x2)
       (push-stack* x3)))
  ([sm x1 x2 x3 & xs]
   (-> sm
       (push-stack* x1)
       (push-stack* x2)
       (push-stack* x3)
       (as-> $ (reduce push-stack* $ xs)))))


(defn update-code
  [sm f & args]
  (let [code (get-code sm)]
    (set-code sm (apply f code args))))


(defn set-word
  [sm wname wfunc]
  (set-word* sm wname wfunc))
