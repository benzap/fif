(ns fif.impl.stack-machine
  "Main implementation of IStackMachine"
  (:require [fif.stack-machine :refer :all]
            [fif.stack-machine.flags :as stack.flags]
            [fif.stack-machine.stash :as stack.stash]
            [fif.stack-machine.words :as stack.words]
            [fif.stack-machine.processor :as stack.processor]
            [fif.stack-machine.error-handling :as error-handling]
            [fif.stack-machine.scope :as stack.scope]
            [fif.utils.scope :as utils.scope]
            [fif.utils.stash :as utils.stash]))


(defrecord StackMachine
  [arg-stack code-stack ret-stack stash
   scope flags words variables modes
   step-num step-max
   system-error-handler
   halt? debug?]
  
  fif.stack-machine/IStackMachine
  
  ;; Main Stack
  (push-stack* [this arg]
    (update-in this [:arg-stack] conj arg))

  (pop-stack [this]
    (as-> this $
      (update-in $ [:arg-stack] pop)))

  (get-stack [this]
    (-> this :arg-stack))

  (set-stack [this stack]
    (assoc this :arg-stack stack))

  (clear-stack [this]
    (assoc this :arg-stack (empty (:arg-stack this))))


  ;; Return Stack
  (push-ret [this x]
    (update-in this [:ret-stack] conj x))

  (pop-ret [this]
    (update-in this [:ret-stack] pop))
  
  (get-ret [this]
    (-> this :ret-stack))

  (clear-ret [this]
    (assoc this :ret-stack (empty (:ret-stack this))))


  ;; Stack Stash

  ;; Deprecated, replaced
  (get-stash [this]
    (-> this :stash2))

  ;; Deprecated, replaced
  (set-stash [this st]
    (assoc this :stash2 st))

  ;; will replace get-stash
  (get-stash2 [this]
    (-> this :stash))

  ;; will replace set-stash
  (set-stash2 [this stash]
    (assoc this :stash stash))


  ;; Scope
  (get-scope [this]
    (:scope this))

  (set-scope [this scope]
    (assoc this :scope scope))


  ;; Temp Macro
  (push-temp-macro [this x]
    (update-in this [:temp-macro-stack] conj x))

  (pop-temp-macro [this]
    (update-in this [:temp-macro-stack] pop))

  (get-temp-macro [this]
    (-> this :temp-macro-stack))

  (set-temp-macro [this st]
    (assoc this :temp-macro-stack st))

  (pick-temp-macro [this]
    (-> this :stash peek))


  ;; Word Dictionary
  (set-word [this wname wbody]
    (stack.words/set-global-word this wname wbody))

  (get-word [this wname]
    (stack.words/get-word this wname))


  ;; Variable Store
  (set-variable [this vname vval]
    (update-in this [:variables] assoc vname vval))

  (get-variables [this]
    (-> this :variables))


  ;; Mode Functions
  (set-mode [this flag modefn]
    (update-in this [:modes] assoc flag modefn))

  (remove-mode [this flag]
    (update-in this [:modes] dissoc flag))


  ;; Mode Flags
  (push-flag [this flag]
    (update-in this [:flags] conj flag))

  (pop-flag [this]
    (update-in this [:flags] pop))

  (get-flags [this]
    (-> this :flags))

  (set-flags [this flags]
    (assoc this :flags flags))


  ;; Code Queue
  (enqueue-code [this arg]
    (update-in this [:code-stack] concat (list arg)))

  (dequeue-code [this]
    (update-in this [:code-stack] #(-> % rest vec)))

  (set-code [this args]
    (assoc this :code-stack args))

  (get-code [this]
    (-> this :code-stack))


  ;; Step Tracker
  (get-step-max [this]
    (-> this :step-max))

  (set-step-max [this m]
    (assoc this :step-max m))

  (inc-step [this]
    (update this :step-num inc))

  (set-step-num [this n]
    (assoc this :step-num n))

  (get-step-num [this]
    (-> this :step-num))


  ;; Debugging
  (enable-debug [this]
    (assoc this :debug? true))

  (disable-debug [this]
    (assoc this :debug? false))

  (is-debug-mode? [this]
    (-> this :debug?))

  ;; Error Handling
  (get-system-error-handler [this]
    (:system-error-handler this))

  (set-system-error-handler [this err-handler]
    (assoc this :system-error-handler err-handler))


  ;; Execution
  (halt [this]
    (assoc this :halt? true))

  (step [this]
    (let [arg (-> this get-code first)]
      (if (stack.flags/has-flags? this)
        (try
          (-> this stack.processor/process-mode inc-step)
          (catch Exception ex
            (error-handling/handle-system-error this ex)))
        (try
          (-> this stack.processor/process-arg inc-step)
          (catch Exception ex
            (error-handling/handle-system-error this ex))))))

  (run [this]
    (loop [sm this]
      (let [step-num (get-step-num sm)
            step-max (get-step-max sm)]
        (if (or (empty? (-> sm get-code))
                (and (> step-max 0) (>= step-num step-max))
                (:halt? sm))
          sm
          (recur (step sm)))))))


(defn new-stack-machine []
  (map->StackMachine
   {:arg-stack '()
    :code-stack '()
    :ret-stack '()
    :temp-macro-stack '()
    :scope (utils.scope/new-scope)
    :stash2 '()
    :stash (utils.stash/create-stash)
    :flags []
    :words {}
    :variables {}
    :modes {}
    :step-num 0
    :step-max 0
    :system-error-handler nil
    :halt? false
    :debug? true}))
