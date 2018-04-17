(ns fif.stack-machine
  (:refer-clojure :exclude [eval]))


(defprotocol IStackMachine
  (push-stack [this arg])
  (pop-stack [this])
  (get-stack [this])
  (set-stack [this stack])
  (clear-stack [this])

  (push-ret [this ret])
  (pop-ret [this])
  (get-ret [this])
  (clear-ret [this])

  ;; Deprecated
  (get-stash [this])
  (set-stash [this st])

  (get-stash2 [this])
  (set-stash2 [this stash])

  (get-scope [this])
  (set-scope [this scope])

  (push-temp-macro [this x])
  (pop-temp-macro [this])
  (get-temp-macro [this])
  (set-temp-macro [this st])
  (pick-temp-macro [this])

  (set-word [this wname wbody])
  (remove-word [this wname])
  (get-words [this])

  (set-variable [this vname vval])
  (get-variables [this])

  (set-mode [this flag modefn])
  (remove-mode [this flag])

  (push-flag [this flag])
  (pop-flag [this])
  (get-flags [this])
  (set-flags [this flags])

  (enqueue-code [this arg])
  (dequeue-code [this])
  (set-code [this stack])
  (get-code [this])

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

  (halt [this])
  (step [this])
  (run [this]))
