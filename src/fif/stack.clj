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

  (step [this])
  (run [this]))


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
      (-> sm (push-stack arg) dequeue-code))
    :else
    (-> sm (push-stack arg) dequeue-code)))


(defn eval-arg [sm arg])
  
    
    
#_(eval-arg (new-stack-machine) (edn/read-string "+"))


(defn eval-fn [sm args]
  (-> sm (set-code (vec (concat (get-code sm) args))) run))


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


(defn take-to-token [coll token]
  (into '() (take-while #(not= % token) coll)))


(defn strip-token [coll token]
  (cond-> coll
   (= (peek coll) token)
   (rest)
   (= (last coll) token)
   (as-> $ (take (dec (count $)) $))))


(defn rest-at-token [coll token]
  (let [idx-token (inc (count (take-to-token coll token)))]
    (reverse (into '() (drop idx-token coll)))))


(comment
 (def x '(0 if 1 1 + else 2 2 + then))
 (def y '(1 if 1 then))

 (take-to-token x 'then)

 (rest-at-token x 'else))


(defn between-tokens [coll start end]
  (-> coll
    (reverse (take-to-token end))
    (rest-at-token start)))


(defn split-at-token [coll token]
  [(take-to-token coll token)
   (reverse (rest-at-token coll token))])


#_(split-at-token '(1 2 'else 3 4) 'else)


(defn replace-token [coll otoken ntoken]
  (->> (for [tok coll]
        (if (= otoken tok) ntoken tok))
      (into '())
      reverse))

(replace-token '(if inner-if some-val inner-then then) 'inner-if 'if)
  


(defn push-coll [coll tokens]
  (reduce (fn [coll token] (conj coll token)) coll (reverse tokens)))


(push-coll '(1 2 3) '(if 1 else 2 then))


#_(split-at-token (between-tokens y 'if 'then) 'else)


(defrecord StackMachine [arg-stack code-stack ret-stack 
                         flags words variables modes]
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

  (step [this]
    (let [arg (-> this get-code first)]
      (if (has-flags? this)
        (-> this (process-mode arg))
        (-> this (process-arg arg)))))

  (run [this]
    (loop [sm this]
      (prn (-> sm get-stack) "--" (-> sm get-code))
      (if-let [arg (-> sm get-code first)]
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
    :modes {}}))
