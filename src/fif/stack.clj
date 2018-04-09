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

  (enqueue-code [this arg])
  (dequeue-code [this])
  (set-code [this stack])
  (get-code [this])

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


(defn take-to-token [coll token]
  (reverse (into '() (take-while #(not= % token) coll))))


(defn strip-token [coll token]
  (cond-> coll
   (= (peek coll) token)
   (rest)
   (= (last coll) token)
   (as-> $ (take (dec (count $)) $))))


(defn rest-at-token [coll token]
  (let [idx-token (inc (count (take-to-token coll token)))]
    (reverse (into '() (drop idx-token coll)))))


(defn between-tokens [coll start end]
  (as-> coll $
    (take-to-token $ end)
    (rest-at-token $ start)))


(defn split-at-token [coll token]
  [(take-to-token coll token)
   (rest-at-token coll token)])


(defn replace-token [coll otoken ntoken]
  (->> (for [tok coll]
        (if (= otoken tok) ntoken tok))
      (into '())
      reverse))


(defn push-coll [coll tokens]
  (reduce (fn [coll token] (conj coll token)) coll (reverse tokens)))


(defrecord StackMachine [arg-stack code-stack ret-stack 
                         flags words variables modes
                         step-num step-max]
  IStackMachine
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

  (push-ret [this x]
    (update-in this [:ret-stack] conj x))

  (pop-ret [this]
    (update-in this [:ret-stack] pop))
  
  (get-ret [this]
    (-> this :ret-stack))

  (clear-ret [this]
    (assoc this :ret-stack (empty (:ret-stack this))))

  (set-word [this wname wbody]
    (update-in this [:words] assoc wname wbody))

  (remove-word [this wname]
    (update-in this [:words] dissoc wname))

  (get-words [this]
    (-> this :words))

  (set-variable [this vname vval]
    (update-in this [:variables] assoc vname vval))

  (get-variables [this]
    (-> this :variables))

  (set-mode [this flag modefn]
    (update-in this [:modes] assoc flag modefn))

  (remove-mode [this flag]
    (update-in this [:modes] dissoc flag))

  (push-flag [this flag]
    (update-in this [:flags] conj flag))

  (pop-flag [this]
    (update-in this [:flags] pop))

  (get-flags [this]
    (-> this :flags))

  (enqueue-code [this arg]
    (update-in this [:code-stack] conj arg))

  (dequeue-code [this]
    (update-in this [:code-stack] #(-> % rest vec)))

  (set-code [this args]
    (assoc this :code-stack args))

  (get-code [this]
    (-> this :code-stack))

  (set-step-max [this m]
    (assoc this :step-max m))

  (inc-step [this]
    (update this :step-num inc))

  (set-step-num [this n]
    (assoc this :step-num n))

  (get-step-num [this]
    (-> this :step-num))

  (step [this]
    (let [arg (-> this get-code first)]
      (if (has-flags? this)
        (-> this (process-mode) inc-step)
        (-> this (process-arg) inc-step))))

  (run [this]
    (loop [sm this]
      (if-not (empty? (-> sm get-code))
        (recur (step sm))
        sm))))


(defn new-stack-machine []
  (map->StackMachine
   {:arg-stack '()
    :code-stack []
    :ret-stack '()
    :flags []
    :words {}
    :variables {}
    :modes {}
    :step-num 0
    :step-max 0}))
