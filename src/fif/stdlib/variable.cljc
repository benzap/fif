(ns fif.stdlib.variable
  "Includes the variable-mode, for creating mutable variables within a
  stack machine."
  (:require
   [fif.stack-machine :as stack]
   [fif.stack-machine.words :as stack.words]
   [fif.stack-machine.mode :as mode]
   [fif.stack-machine.stash :as stash]))


(def arg-global-var-token 'def)
(def arg-local-var-token 'let)
(def variable-mode-flag :variable-mode)


(defn wrap-global-variable
  [value]
  (fn [sm]
    (-> sm
        stack/dequeue-code
        (stack/update-code #(concat %2 %1) [value]))))


(defn wrap-local-variable
  [value]
  (fn [sm]
    (-> sm
        stack/dequeue-code
        (stack/update-code #(concat %2 %1) [value]))))


(defn enter-variable-mode
  [sm stash]
  (mode/enter-mode sm variable-mode-flag stash))


(defn exit-variable-mode
  [sm]
  (mode/exit-mode sm))


(defmulti variable-mode mode/mode-dispatch-fn)


;; Global Variable Implementation


(defn start-global-variable
  "Puts the stack machine into variable-mode"
  [sm]
  (-> sm
      (enter-variable-mode {:op ::global-variable :op-state ::get-symbol})
      stack/dequeue-code))


(defmethod variable-mode
  {:op ::global-variable :op-state ::get-symbol}
  [sm]
  (let [arg (-> sm stack/get-code first)]
    (if (symbol? arg)
      (-> sm
          (stack/push-stack arg)
          (mode/update-state assoc :op-state ::set)
          stack/dequeue-code)

      ;; TODO: throw stack error
      (-> sm
          (stack/push-stack "Def: Undefined Behaviour!")
          (stack/halt)))))


(defmethod variable-mode
  {:op ::global-variable :op-state ::set}
  [sm]
  (let [val (-> sm stack/get-code first)
        sym (-> sm stack/get-stack peek)]
    (-> sm
        (stack.words/set-global-word-defn
         sym (wrap-global-variable val)
         :variable? true)
        stack/pop-stack
        exit-variable-mode
        stack/dequeue-code)))


;; Local Variable Implementation


(defn start-local-variable
  "Puts the stack machine into variable-mode to create a local
  variable."
  [sm]
  (-> sm
      (enter-variable-mode {:op ::local-variable :op-state ::get-symbol})
      stack/dequeue-code))


(defmethod variable-mode
  {:op ::local-variable :op-state ::get-symbol}
  [sm]
  (let [arg (-> sm stack/get-code first)]
    (if (symbol? arg)
      (-> sm
          (stack/push-stack arg)
          (mode/update-state assoc :op-state ::set)
          stack/dequeue-code)

      ;; TODO: throw stack error
      (-> sm
          (stack/push-stack "Def: Undefined Behaviour!")
          (stack/halt)))))


(defmethod variable-mode
  {:op ::local-variable :op-state ::set}
  [sm]
  (let [val (-> sm stack/get-code first)
        sym (-> sm stack/get-stack peek)]
    (-> sm
        (stack.words/set-word sym (wrap-local-variable val))
        stack/pop-stack
        exit-variable-mode
        stack/dequeue-code)))


(defn setg
  "Word function used to set a global variable to a provided value"
  [sm]
  (let [[val sym] (stack/get-stack sm)]
    ;; TODO: check if variable exists
    (-> sm
        stack/pop-stack
        stack/pop-stack
        (stack.words/set-global-word-defn
         sym (wrap-global-variable val)
         :variable? true)
        stack/dequeue-code)))


(defn setl
  "Word function used to set a local variable to a provided value"
  [sm]
  (let [[val sym] (stack/get-stack sm)]
    ;; TODO: check if variable exists
    (-> sm
        stack/pop-stack
        stack/pop-stack
        (stack.words/set-word sym (wrap-local-variable val))
        stack/dequeue-code)))


(defn import-stdlib-variable-mode
  "Stack Machine Import for variable-mode"
  [sm]
  (-> sm
      (stack/set-word arg-global-var-token start-global-variable)
      (stack/set-word 'setg setg)
      (stack/set-word arg-local-var-token start-local-variable)
      (stack/set-word 'setl setl)
      (stack/set-mode variable-mode-flag variable-mode)))
