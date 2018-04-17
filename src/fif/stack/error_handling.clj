(ns fif.stack.error-handling
  "Functions for handling errors within the fif stackmachine."
  (:require
   [fif.stack :as stack]))


(def error-symbol 'ERR##)


(defn new-error-object
  "Creates a new error object"
  ([msg extra] [error-symbol msg extra])
  ([msg] (new-error-object msg {})))


(defn error-object? [obj]
  (-> obj first (= error-symbol)))


(defn stack-error
  "Creates an error object for stack errors."
  ;; TODO: include more info when in debug mode
  ([sm msg extra]
   (let [stack-info {:type ::stack-error}]
     (new-error-object msg (merge stack-info extra))))
  ([sm msg] (stack-error sm msg {})))


(defn stack-error-object?
  "Returns true if the given object is a stack error object"
  [obj]
  (and (error-object? obj)
       (-> obj (nth 2) :type (= ::stack-error))))


(defn system-error
  "Creates an error object for system errors."
  ;; TODO: include more info when in debug mode
  ([sm ex msg extra]
   (let [stack-info {:type ::system-error
                     :stack (-> sm stack/get-stack reverse)
                     :word (-> sm stack/get-code first)
                     :ex-data (ex-data ex)
                     :ex-message (.getMessage ex)}]
     (new-error-object msg (merge stack-info extra))))
  ([sm ex msg] (system-error sm ex msg {})))
  

(defn system-error-object?
  "Returns true if the given object is a system error object"
  [obj]
  (and (error-object? obj)
       (-> obj (nth 2) :type (= ::system-error))))


(defn set-error
  "Places the given error object on the stack, and halts the stack
  machine."
  [sm errobj]
  (-> sm
      (stack/push-stack errobj)
      stack/halt))


(defn default-system-error-handler
  "The default error handler for system errors that the fif
  stackmachine experiences.

  Keyword Arguments:

  sm - The Stackmachine instance at the time of the system error

  ex - An exception object

  Notes:
  
  - System error is re-thrown if the stackmachine is not in debug-mode."
  [sm ex]
  (if (stack/is-debug-mode? sm)
    (let [errmsg (str "System Error")
          errobj (system-error sm ex errmsg)]
      (-> sm
          (set-error errobj)))
    (throw ex)))


(defn handle-system-error [sm ex]
  (if-let [system-error-handler (stack/get-system-error-handler sm)]
    (system-error-handler sm ex)
    (throw ex)))
