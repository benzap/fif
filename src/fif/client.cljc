(ns fif.client
  "Functions for better interoperability between fif and a
  clojure(script) client."
  (:require [clojure.string :as str]))


(def ^:dynamic *fif-clojure-value-escape* '%=)


(defmacro form
  "quoted form with escaped evaluation. Values preceding
  *fif-clojure-value-escape* are evaluated in clojure(script).

  Examples:

  ;; Assuming we want to pull a clojure value into the a quoted form
  (def x 10)
  (form value %= x) ;; => '[form value 10]

  Notes:

  - *fif-clojure-value-escape can be replaced with a different escape
  symbol as desired."
  [& body]
  (:result
    (reduce
      (fn [{:keys [result eval-next?]} atom]
        (cond
          (= atom *fif-clojure-value-escape*)
          {:result result
           :eval-next? true}
          eval-next?
          {:result (conj result atom)}
          :else
          {:result (conj result `(quote ~atom))}))
      {:result []}
      body)))


(defmacro form-string
  "Equivlant to `form`, but presents the result as a string that can
  be consumed by a fif stack-machine."
  [& body]
  `(let [sform# (pr-str (form ~@body))]
     ;; Remove surrounding vector
     (subs sform# 1 (dec (count sform#)))))
