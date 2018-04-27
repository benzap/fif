(ns fif.stdlib.compile
  "Defines the two main modes, compile-mode and function-mode, which are
  used to define new word definitions, and to invoke word definitions
  with their own word definition scope.

  compile-mode is called when a function is first defined with
  `arg-start-token`, and this compilation mode is finished upon
  reaching the end of the new definition defined by `arg-end-token`.

  function-mode is entered when a word definition is invoked from the
  code queue."
  (:require [fif.stack-machine :as stack]
            [fif.stack-machine.scope :as stack.scope]
            [fif.stack-machine.stash :as stack.stash]
            [fif.stack-machine.processor :as stack.processor]
            [fif.stdlib.reserved :as reserved]))


(def arg-start-token reserved/function-begin-definition-word)
(def arg-end-token reserved/function-end-definition-word)
(def arg-end-function-token 'compile/end-function)
(def compile-mode-flag :compile-mode)
(def inner-compile-mode-flag :inner-compile-mode)
(def function-mode-flag :function-mode)


(defn wrap-compiled-fn
  "Used to wrap a word definition for use within the stack
  machine. `wbody` is a collection of words which make up the word
  definition. This function is then placed within the stack machine
  using fif.stack-machine/set-word.

  Notes:

  - Note that invoking this wrapper, places the stack machine in the
  function-mode, which creates a new scope. This scope serves the
  purpose of storing local variables for that function, until it
  leaves function-mode."
  [wbody]
  (fn [sm]
    (-> sm
        (stack/push-flag function-mode-flag)
        (stack.scope/new-scope)
        (stack/set-code
         (concat wbody [arg-end-function-token]
                 (-> sm stack/dequeue-code stack/get-code))))))
                 

(defn inner-compile-mode
  "inner-compile mode is used to circumvent issues with inner function
  definitions. It is used to ignore these inner function definitions
  by placing them into a new mode of operation where it places the
  word values onto the stack."
  [sm]
  (let [arg (-> sm stack/get-code first)]
    (cond
      (= arg arg-start-token)
      (-> sm
          (stack/push-flag inner-compile-mode-flag)
          (stack.stash/update-stash conj arg)
          stack/dequeue-code)

      (= arg arg-end-token)
      (-> sm
          stack/pop-flag
          (stack.stash/update-stash conj arg)
          stack/dequeue-code)

      :else
      (-> sm
          (stack.stash/update-stash conj arg)
          stack/dequeue-code))))


(defn compile-mode
  "Represents the compilation mode"
  [sm]
  (let [arg (-> sm stack/get-code first)]
    (cond
      (= arg arg-start-token)
      (-> sm
          (stack.stash/update-stash conj arg)
          (stack/push-flag inner-compile-mode-flag)
          stack/dequeue-code)

      (= arg arg-end-token)
      (let [fn-content (stack.stash/peek-stash sm)
            [wname & wbody] fn-content]
        (-> sm
            (stack/set-word wname (wrap-compiled-fn wbody))
            (stack.stash/remove-stash)
            stack/pop-flag
            stack/dequeue-code))

      :else
      (-> sm
          (stack.stash/update-stash conj arg)
          stack/dequeue-code))))


(defn function-mode
  ""
  [sm]
  (let [arg (-> sm stack/get-code first)]
    (cond
      (= arg arg-end-function-token)
      (-> sm
          (stack/pop-flag)
          (stack.scope/remove-scope)
          (stack/dequeue-code))

      :else
      (-> sm stack.processor/process-arg))))


(defn start-defn
  "We retrieved the start-token word, and we push it onto the stack and
   set our compile flag"
  [sm]
  (-> sm
      (stack/push-flag compile-mode-flag)
      (stack.stash/new-stash [])
      stack/dequeue-code))


(defn import-stdlib-compile-mode [sm]
  (-> sm
      (stack/set-word arg-start-token start-defn)
      (stack/set-mode compile-mode-flag compile-mode)
      (stack/set-mode inner-compile-mode-flag inner-compile-mode)
      (stack/set-mode function-mode-flag function-mode)))
  
