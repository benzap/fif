(ns fif.token
  "Includes functions for manipulating tokens/symbols.")


(defn take-to-token
  [coll token]
  (reverse (into '() (take-while #(not= % token) coll))))


(defn strip-token [coll token]
  (cond-> coll
   (= (peek coll) token)
   (rest)
   (= (last coll) token)
   (as-> $ (take (dec (count $)) $))))


(defn rest-at-token [coll token]
  (let [idx-token (inc (count (take-to-token coll token)))]
    (reverse (into '() (drop idx-token coll)))))


(defn between-tokens [coll start end]
  (as-> coll $
    (take-to-token $ end)
    (rest-at-token $ start)))


(defn split-at-token [coll token]
  [(take-to-token coll token)
   (rest-at-token coll token)])


(defn replace-token [coll otoken ntoken]
  (->> (for [tok coll]
         (if (= otoken tok) ntoken tok))
       (into '())
       reverse))


(defn push-coll [coll tokens]
  (reduce (fn [coll token] (conj coll token)) coll (reverse tokens)))
