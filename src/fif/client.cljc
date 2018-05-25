(ns fif.client
  "Functions for better interoperability between fif and a
  clojure(script) client.")


(def ^:dynamic *fif-clojure-value-escape* '%=)


(defn form-fn
  "Provides an escape symbol to evaluate clojure code within a quoted
  form. See `form` macro for use-cases.

  Notes:

  - Escape symbol is defined as *fif-clojure-value-escape*."
  [& body]
  (loop [new-body []
         state :grab
         idx 0]
    (cond
      (>= idx (count body))
      new-body
        
      (= state :fvalue)
      (recur
       (conj new-body (eval (nth body idx)))
       :grab
       (inc idx))

      (= *fif-clojure-value-escape* (nth body idx))
      (recur
       new-body
       :fvalue
       (inc idx))

      :else
      (recur
       (conj new-body (nth body idx))
       :grab
       (inc idx)))))


(defmacro form-old
  "Generate a quoted form, with clojure values that can be overidden for
  evaluation with `*fif-clojure-value-escape*`

  Examples:

  (def x 10)

  `(test value x) ;; => (test value x)

  ;; To expose the x value, escape it.

  (form test value %= x) ;; => (test value 10)

  Notes:

  - Useful for mixing in clojure(script) for more dynamic fif scripts.
  
  (require '[fif.core :refer [eval-fn]])
  (require '[fif.client :refer [form]])

  (defn print-name [name]
    (eval-fn (form %= name \"!\" str println)))

  (print-name \"Timmy\")

  - Is also considered useful for communicating with a remote fif
  stackmachine.
"
  [& body]
  `(apply form-fn (quote ~body)))


#_(def x 10)
#_(form test value %= x 10)


