(ns fif.stack
  (:refer-clojure :exclude [eval])
  (:require [clojure.tools.reader.edn :as edn]))


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

  (push-stash [this x])
  (pop-stash [this])
  (get-stash [this])
  (set-stash [this st])
  (pick-stash [this])

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

  (step [this])
  (run [this]))


(defn has-flags? [sm]
  (not (empty? (get-flags sm))))
  

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


(defn create-sub-stack [coll]
  (conj coll '()))


(defn push-sub-stack [coll x]
  (let [f (conj (peek coll) x)]
    (-> coll pop (conj f))))


(defn pop-sub-stack [coll]
  (let [f (-> coll peek pop)]
    (-> coll pop (conj f))))


(defn get-sub-stack [coll]
  (-> coll peek))


(defn set-sub-stack [coll x]
  (-> coll pop (conj x)))


(defn remove-sub-stack [coll]
  (pop coll))


(defrecord StackMachine [arg-stack code-stack ret-stack 
                         stash
                         flags words variables modes
                         step-num step-max]
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
  (push-stash [this x]
    (update-in this [:stash] conj x))

  (pop-stash [this]
    (update-in this [:stash] pop))

  (get-stash [this]
    (-> this :stash))

  (set-stash [this st]
    (assoc this :stash st))

  (pick-stash [this]
    (-> this :stash peek))


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
    (update-in this [:code-stack] conj arg))

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


  ;; Execution
  (step [this]
    (let [arg (-> this get-code first)]
      (if (has-flags? this)
        (-> this process-mode inc-step)
        (-> this process-arg inc-step))))

  (run [this]
    (loop [sm this]
      (let [step-num (get-step-num sm)
            step-max (get-step-max sm)]
        (if (or (empty? (-> sm get-code))
                (and (> step-max 0) (>= step-num step-max)))
          sm
          (recur (step sm)))))))


(defn new-stack-machine []
  (map->StackMachine
   {:arg-stack '()
    :code-stack []
    :ret-stack '()
    :temp-macro-stack '()
    :stash '()
    :flags []
    :words {}
    :variables {}
    :modes {}
    :step-num 0
    :step-max 0}))
