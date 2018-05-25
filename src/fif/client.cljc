(ns fif.client
  "Functions for better interoperability between fif and a
  clojure(script) client.")


(def ^:dynamic *fif-clojure-value-escape* '%=)


(defmacro form
  "quoted form with escaped evaluation. Values preceding
  *fif-clojure-value-escape* are evaluated.

  Examples:"
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


#_(def x 10)
#_(form test value %= x 10)


