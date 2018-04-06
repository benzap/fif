(ns fif.stack
  (:refer-clojure :exclude [eval])
  (:require [clojure.tools.reader.edn :as edn]))


(defprotocol IStack
  (push-stack [this arg])
  (pop-stack [this])
  (get-stack [this])
  (clear-stack [this])

  (push-ret [this ret])
  (pop-ret [this])
  (get-ret [this])
  (clear-ret [this])

  (set-word [this wname wbody])
  (remove-word [this wname])
  (get-words [this])

  (set-mode [this flag modefn])
  (remove-mode [this flag])

  (push-flag [this flag])
  (pop-flag [this])
  (get-flags [this]))


(defrecord StackMachine [words arg-stack ret-stack flags]
  IStack
  (push-stack [this arg]
    (update-in this [:arg-stack] conj arg))

  (pop-stack [this]
    (as-> this $
      (update-in $ [:arg-stack] pop)))

  (get-stack [this]
    (-> this :arg-stack))

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

  (set-mode [this flag modefn]
    (update-in this [:modes] assoc flag modefn))

  (remove-mode [this flag]
    (update-in this [:modes] dissoc flag))

  (push-flag [this flag]
    (update-in this [:flags] conj flag))

  (pop-flag [this]
    (update-in this [:flags] pop))

  (get-flags [this]
    (-> this :flags)))


(defn new-stack-machine []
  (map->StackMachine
   {:arg-stack '()
    :ret-stack '()
    :flags []
    :words {}
    :modes {}}))


(defn has-flags? [sm]
  (not (empty? (get-flags sm))))
  

(defn process-mode [sm arg]
  (let [current-mode (peek (get-flags sm))]
    (if-let [modefn (-> sm :modes (get current-mode))]
      (modefn sm arg)
      (throw (ex-info "Unable to find mode function for flagged mode: " current-mode)))))


(defn process-arg [sm arg]
  (cond
    (symbol? arg)
    (if-let [wfn (-> sm get-words (get arg))]
      (wfn sm)
      (push-stack sm arg))
    :else
    (push-stack sm arg)))


(defn eval-arg [sm arg]
  (if (has-flags? sm)
   (process-mode sm arg)
   (process-arg sm arg)))
    
#_(eval-arg (new-stack-machine) (edn/read-string "+"))


(defn eval-fn [sm args]
  (reduce (fn [sm arg] (eval-arg sm arg)) sm args))


(defmacro eval [sm & body]
  `(eval-fn ~sm (quote ~body)))


#_(macroexpand-1 '(eval (new-stack-machine) 1 2 3 +))
#_(eval (new-stack-machine) 1 2 3 +)
#_(-> (edn/read-string "(1 2 3 +)") last symbol?)


(defn wrap-eval-string [s]
  (str "[" s "]"))


(defn eval-string [sm s]
  (->> s
       wrap-eval-string
       edn/read-string
       (eval-fn sm)))

#_(eval-string (new-stack-machine) "1 2 3")
