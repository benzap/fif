(ns fif.stack-machine
  (:refer-clojure :exclude [eval]))


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

  ;; Will Replace Old Functionality
  (get-stash2 [this])
  (set-stash2 [this stash])

  (get-scope [this])
  (set-scope [this scope])

  ;; Soon-to-be Deprecated
  (push-temp-macro [this x])
  (pop-temp-macro [this])
  (get-temp-macro [this])
  (set-temp-macro [this st])
  (pick-temp-macro [this])

  (set-word [this wname wbody])
  (get-word [this wname])

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
