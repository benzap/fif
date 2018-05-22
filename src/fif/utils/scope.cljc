(ns fif.utils.scope
  "Implements a container system which allows for retrieval of values
  within a hierarchy of containers, where each container is a separate
  scope.")


(defn new-scope
  ([scope]
   (conj scope {}))
  ([] (new-scope [])))


(defn update-scope
  [scope f & args]
  (apply update-in scope [(dec (count scope))] f args))


(defn remove-scope
  [scope]
  (pop scope))
  

(def not-found ::miss)
(defn get-in-scope
  ([scope attrs default]
   (as-> scope $ (map #(get-in % attrs not-found) $)
         (filter #(not= % not-found) $)
         (reverse $)
         (if (empty? $) default (first $))))
  ([scope attrs] (get-in-scope scope attrs nil)))


(defn update-global-scope
  [scope f & args]
  (apply update-in scope [0] f args))


(defn get-merged-scope
  [scope]
  (reduce merge scope))
