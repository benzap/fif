(ns fif.utils.token
  "Includes functions for manipulating tokens/symbols."
  (:require [clojure.string :as str]))


(defn take-to-token
  "Takes from a collection, up to the first `token`"
  [coll token]
  (apply list (take-while #(not= % token) coll)))


(defn strip-token
  "Removes the given `token` from the start and end of the collection"
  [coll token]
  (cond-> coll
   (= (peek coll) token)
   (rest)
   (= (last coll) token)
   (as-> $ (take (dec (count $)) $))))


(defn rest-at-token
  "Returns the rest of the provided collection, which is not containing
  the first set of tokens up to and not containing `token`"
  [coll token]
  (let [idx-token (inc (count (take-to-token coll token)))]
    (apply list (drop idx-token coll))))


(defn between-tokens
  "Returns a collection which is the first subset of the provided
  collection between the `start` token and `end` token."
  [coll start end]
  (as-> coll $
    (take-to-token $ end)
    (rest-at-token $ start)))


(defn split-at-token
  "Returns a vector pair, with the first element being the collection up
  to the provided `token`, and the second element being the rest of
  the collection after the provided `token`."
  [coll token]
  [(take-to-token coll token)
   (rest-at-token coll token)])


(defn replace-token
  "Replace all instances of the old token `otoken` with a new token
  `ntoken`."
  [coll otoken ntoken]
  (->> (for [tok coll]
         (if (= otoken tok) ntoken tok))
       (apply list)))


(defn push-coll
  ""
  [coll tokens]
  (reduce (fn [coll token] (conj coll token)) coll (reverse tokens)))


(defn symbol-starts-with?
  "Returns true if the given symbol `sym` starts with the string/symbol `s`"
  [sym s]
  (str/starts-with? (str sym) (str s)))
  

(defn symbol-ltrim-once
  "Trim the subsymbol `s` from the start of `sym` once."
  [sym s]
  (if (symbol-starts-with? sym s)
    (-> sym
        str (subs (count (str s)))
        symbol)
    (symbol sym)))
