(ns fif.impl.stack-machine
  "Main implementation of IStackMachine"
  (:require
   [fif.stack-machine :as stack]
   [fif.stack-machine.flags :as stack.flags]
   [fif.stack-machine.stash :as stack.stash]
   [fif.stack-machine.words :as stack.words]
   [fif.stack-machine.variable :as stack.variable]
   [fif.stack-machine.exceptions :as stack.exceptions]

   [fif.stack-machine.processor :as stack.processor]
   [fif.stack-machine.error-handling :as error-handling]
   [fif.stack-machine.scope :as stack.scope]
   [fif.utils.scope :as utils.scope]
   [fif.utils.stash :as utils.stash]))


(defrecord StackMachine
  [arg-stack
   code-stack
   ret-stack
   temp-macro-stack
   scope
   sub-stash
   mode-stash
   flags
   modes
   step-num
   step-max
   system-error-handler
   stack-error-handler
   halt?
   debug?]
  
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
    (-> this :sub-stash))

  ;; Deprecated, replaced
  (set-stash [this st]
    (assoc this :sub-stash st))

  (clear-stash [this]
    (assoc this :sub-stash (empty (:sub-stash this))))

  ;; will replace get-stash
  (get-mode-stash [this]
    (-> this :mode-stash))

  ;; will replace set-stash
  (set-mode-stash [this stash]
    (assoc this :mode-stash stash))
  
  (clear-mode-stash [this]
    (stack.stash/clear-stash this))


  ;; Scope
  (get-scope [this]
    (:scope this))

  (set-scope [this scope]
    (assoc this :scope scope))

  (clear-scope [this]
    (stack.scope/clear-scope this))


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
    (-> this :temp-macro-stack peek))

  (clear-temp-macro [this]
    (assoc this :temp-macro-stack (empty (stack/get-temp-macro this))))


  ;; Word Dictionary
  (set-word* [this wname wbody]
    (stack.words/set-global-word this wname wbody))

  (get-word [this wname]
    (stack.words/get-word this wname))

  (get-word-listing [this]
    (stack.words/get-word-listing this))

  (set-variable [this wname value]
    (stack.words/set-global-word-defn
     this wname (stack.variable/wrap-global-variable value)
     :variable? true))


  ;; Word Metadata
  (set-word-metadata [this wname wmeta]
    (stack.words/set-global-metadata this wname wmeta))

  (get-word-metadata [this wname]
    (stack.words/get-global-metadata this wname))


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

  (clear-flags [this]
    (assoc this :flags (empty (:flags this))))


  ;; Code Queue
  (push-code [this arg]
    (assoc-in this [:code-stack] (vec (concat [arg] (:code-stack this)))))

  (enqueue-code [this arg]
    (-> this
        (update-in [:code-stack] concat (list arg))
        (update-in [:code-stack] vec)))

  (dequeue-code [this]
    (update-in this [:code-stack] #(-> % rest vec)))

  (set-code [this args]
    (assoc this :code-stack args))

  (get-code [this]
    (-> this :code-stack))

  (clear-code [this]
    (assoc this :code-stack (empty (:code-stack this))))


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

  (get-stack-error-handler [this]
    (:stack-error-handler this))

  (set-stack-error-handler [this err-handler]
    (assoc this :stack-error-handler err-handler))


  ;; Execution
  (halt [this]
    (assoc this :halt? true))

  (step [this]
    (let [arg (-> this stack/get-code first)]
      (if (stack.flags/has-flags? this)
        (try
          (-> this stack.processor/process-mode stack/inc-step)
          (catch #?(:clj Exception :cljs js/Error) ex
            (error-handling/handle-system-error this ex)))
        (try
          (-> this stack.processor/process-arg stack/inc-step)
          (catch #?(:clj Exception :cljs js/Error) ex
            (error-handling/handle-system-error this ex))))))

  (run [this]
    (loop [sm this]
      (let [step-num (stack/get-step-num sm)
            step-max (stack/get-step-max sm)]
        (cond
          (empty? (-> sm stack/get-code)) sm

          (and (> step-max 0) (>= step-num step-max))
          (stack.exceptions/raise-max-steps-exceeded sm)

          (:halt? sm) sm

          :else (recur (stack/step sm))))))

  (continue [this]
    (assoc this :halt? false)))


(defn new-stack-machine []
  (map->StackMachine
   {:arg-stack '()
    :code-stack '()
    :ret-stack '()
    :temp-macro-stack '()
    :scope (utils.scope/new-scope)
    :sub-stash '()
    :mode-stash (utils.stash/create-stash)
    :flags []
    :modes {}
    :step-num 0
    :step-max 0
    :system-error-handler nil
    :stack-error-handler nil
    :halt? false
    :debug? true}))
