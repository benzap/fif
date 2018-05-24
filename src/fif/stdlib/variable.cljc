(ns fif.stdlib.variable
  "Includes the variable-mode, for creating mutable variables within a
  stack machine."
  (:require
   [fif.stack-machine :as stack]
   [fif.stack-machine.words :as stack.words
    :refer [set-global-word-defn
            set-local-word-defn]]
   [fif.stack-machine.exceptions :as exceptions]
   [fif.stack-machine.mode :as mode]
   [fif.stack-machine.stash :as stash]
   [fif.stack-machine.variable :refer [wrap-global-variable
                                       wrap-local-variable]]))


(def arg-global-var-token 'def)
(def arg-local-var-token 'let)
(def variable-mode-flag :variable-mode)


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
         :variable? :global)
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
        (stack.words/set-local-word-defn
         sym (wrap-local-variable val)
         :variable? :local)
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
         :variable? :global)
        stack/dequeue-code)))


(defn setl
  "Word function used to set a local variable to a provided value"
  [sm]
  (let [[val sym] (stack/get-stack sm)]
    ;; TODO: check if variable exists
    (-> sm
        stack/pop-stack
        stack/pop-stack
        (stack.words/set-local-word-defn
         sym (wrap-local-variable val)
         :variable? :local)
        stack/dequeue-code)))


(defn import-stdlib-variable-mode
  "Stack Machine Import for variable-mode"
  [sm]
  (-> sm

      (set-global-word-defn
       arg-global-var-token start-global-variable
       :stdlib? true
       :doc "def <word> <val> -- Set global variable."
       :group :stdlib.variable)

      (set-global-word-defn
       'setg setg
       :stdlib? true
       :doc "( wname val -- ) Set global variable."
       :group :stdlib.variable)

      (set-global-word-defn
       arg-local-var-token start-local-variable
       :stdlib? true
       :doc "let <word> <val> --  Set local variable."
       :group :stdlib.variable)

      (set-global-word-defn
       'setl setl
       :stdlib? true
       :doc "( wname val -- ) Set local variable."
       :group :stdlib.variable)

      (stack/set-mode variable-mode-flag variable-mode)))
