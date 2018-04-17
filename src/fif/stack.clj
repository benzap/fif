(ns fif.stack
  (:refer-clojure :exclude [eval])
  (:require
   [clojure.tools.reader.edn :as edn]
   [fif.utils.scope :as scope]
   [fif.utils.stash :as stash]))


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
  (push-stash [this x])
  (pop-stash [this])
  (get-stash [this])
  (set-stash [this st])
  (pick-stash [this])

  (get-stash2 [this])
  (set-stash2 [this stash])

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

  (get-system-error-handler [this])
  (set-system-error-handler [this err-handler])

  (halt [this])
  (step [this])
  (run [this]))


(defn has-flags? [sm]
  (not (empty? (get-flags sm))))
  

(defn debug-mode? [sm]
  (-> sm :debug?))


(defn process-mode [sm]
  (let [arg (-> sm get-code first)
        current-mode (peek (get-flags sm))]
    (if-let [modefn (-> sm :modes (get current-mode))]
      (modefn sm)
      (throw (ex-info "Unable to find mode function for flagged mode: " current-mode)))))


(defn process-arg [sm]
  (let [arg (-> sm get-code first)]
    (cond
      (symbol? arg)
      (if-let [wfn (-> sm get-words (get arg))]
        (wfn sm)
        (-> sm (push-stack arg) dequeue-code))
      :else
      (-> sm (push-stack arg) dequeue-code))))


(defn eval-fn [sm args]
  (-> sm (set-code (vec (concat (get-code sm) args))) run))


(defmacro eval [sm & body]
  `(eval-fn ~sm (quote ~body)))


(defn wrap-eval-string [s]
  (str "[" s "]"))


(defn eval-string [sm s]
  (->> s
       wrap-eval-string
       edn/read-string
       (eval-fn sm)))


(defn handle-system-error [sm ex]
  (if-let [system-error-handler (get-system-error-handler sm)]
    (system-error-handler sm ex)
    (throw ex)))


(defrecord StackMachine
  [arg-stack code-stack ret-stack stash
   scope flags words variables modes
   step-num step-max
   system-error-handler
   halt? debug?]
  
  IStackMachine
  
  ;; Main Stack
  (push-stack [this arg]
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

  ;; Deprecated
  (push-stash [this x]
    (update-in this [:stash2] conj x))

  ;; Deprecated
  (pop-stash [this]
    (update-in this [:stash2] pop))

  ;; Deprecated, replaced
  (get-stash [this]
    (-> this :stash2))

  ;; Deprecated, replaced
  (set-stash [this st]
    (assoc this :stash2 st))

  ;; Deprecated
  (pick-stash [this]
    (-> this :stash2 peek))

  ;; will replace get-stash
  (get-stash2 [this]
    (-> this :stash))

  ;; will replace set-stash
  (set-stash2 [this stash]
    (assoc this :stash stash))


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
    (update-in this [:words] assoc wname wbody))

  (remove-word [this wname]
    (update-in this [:words] dissoc wname))

  (get-words [this]
    (-> this :words))


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
      (if (has-flags? this)
        (try
          (-> this process-mode inc-step)
          (catch Exception ex
            (handle-system-error this ex)))
        (try
          (-> this process-arg inc-step)
          (catch Exception ex
            (handle-system-error this ex))))))

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
    :scope (scope/new-scope)
    :stash2 '()
    :stash (stash/create-stash)
    :flags []
    :words {}
    :variables {}
    :modes {}
    :step-num 0
    :step-max 0
    :system-error-handler nil
    :halt? false
    :debug? true}))
