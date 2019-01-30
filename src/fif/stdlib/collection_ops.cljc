(ns fif.stdlib.collection-ops
  "Includes the majority of clojure collection functions, which have
  been ported to fif for use with the same collections.

  All collection operations follow the same argument order:

  Examples:
  (require '[fif.core :as fif])

  ;; Clojure
  (conj [1 2 3] 4) ;; => [1 2 3 4]

  ;; Fif
  (fif/reval [1 2 3] 4 conj) ;; => '([1 2 3 4])

  "
  (:require
   [fif.stack-machine :as stack]
   [fif.stack-machine.words :as words :refer [set-global-word-defn]]
   [fif.def :refer [wrap-function-with-arity
                    wrap-procedure-with-arity]]))


(defn pair
  "Turns the two arguments into a vector of the two arguments."
  [x y]
  [x y])


(defn op-unpair
  "Takes the top value, which is a vector of two-elements, and places
  the two elements on the main stack."
  [sm]
  (let [[x] (stack/get-stack sm)]
    (-> sm
        stack/pop-stack
        (stack/push-stack (first x))
        (stack/push-stack (second x))
        stack/dequeue-code)))


(defn apply-op
  "Takes the top value, which is a collection of values, and places them
  at the front of the code queue. Places any non-collection values on
  the code queue."
  [sm]
  (let [[coll] (stack/get-stack sm)
        coll (if (coll? coll) coll [coll])]
    (-> sm
        stack/dequeue-code
        stack/pop-stack
        (stack/update-code #(concat %2 %1) coll))))


(defn import-stdlib-collection-ops
  "Imports the collection operators as part of the standard library."
  [sm]
  (-> sm

      (set-global-word-defn
       'apply apply-op
       :stdlib? true
       :doc "( coll - any.. ) Places the elements of the top value collection onto the stack."
       :group :stdlib.collection)

      (set-global-word-defn
       'assoc (wrap-function-with-arity 3 assoc)
       :stdlib? true
       :doc "( coll key val -- coll ) Associates the `key` - `val` pair into the collection `coll`."
       :group :stdlib.collection)

      (set-global-word-defn
       'assoc-in (wrap-function-with-arity 3 assoc-in)
       :stdlib? true
       :doc "( coll kv val -- coll ) Associates at key vector `kv` with value `val` into the `coll`."
       :group :stdlib.collection)

      (set-global-word-defn
       'butlast (wrap-function-with-arity 1 butlast)
       :stdlib? true
       :doc "( coll -- coll ) Returns a seq of all but the last item."
       :group :stdlib.collection)

      (set-global-word-defn
       'concat (wrap-function-with-arity 2 concat)
       :stdlib? true
       :doc "( coll coll -- coll ) Returns a lazy sequence of the concatentation of the top two collections on the stack."
       :group :stdlib.collection)

      (set-global-word-defn
       'conj (wrap-function-with-arity 2 conj)
       :stdlib? true
       :doc "( coll x -- coll ) Conjoin. Returns a new collection with the addition of `x` to `coll`."
       :group :stdlib.collection)

      (set-global-word-defn
       'cons (wrap-function-with-arity 2 cons)
       :stdlib? true
       :doc "( coll x -- coll ) Returns a new seq where `x` is the new first element of `coll`."
       :group :stdlib.collection)

      (set-global-word-defn
       'contains? (wrap-function-with-arity 2 contains?)
       :stdlib? true
       :doc "( coll key -- b ) Returns true if `key` is present in the given collection `coll`."
       :group :stdlib.collection)

      (set-global-word-defn
       'dedupe (wrap-function-with-arity 1 dedupe)
       :stdlib? true
       :doc "( coll -- coll ) Returns a lazy-seq removing consecutive duplicates in `coll`."
       :group :stdlib.collection)

      (set-global-word-defn
       'dissoc (wrap-function-with-arity 2 dissoc)
       :stdlib? true
       :doc "( coll key -- coll ) Returns a new map which has dissociated with `key`."
       :group :stdlib.collection)

      (set-global-word-defn
       'distinct (wrap-function-with-arity 1 distinct)
       :stdlib? true
       :doc "( coll -- coll ) Returns a lazy-seq of the elements with duplicates removed."
       :group :stdlib.collection)

      (set-global-word-defn
       'drop-last (wrap-function-with-arity 1 drop-last)
       :stdlib? true
       :doc "( coll -- coll ) Returns a lazy-seq of all but the last item."
       :group :stdlib.collection)

      (set-global-word-defn
       'drop-last-n (wrap-function-with-arity 2 drop-last)
       :stdlib? true
       :doc "( coll n -- coll ) Returns a lazy-seq of all but the last `n` items."
       :group :stdlib.collection)

      (set-global-word-defn
       'empty (wrap-function-with-arity 1 empty)
       :stdlib? true
       :doc "( coll -- coll ) Returns an empty collection of the provided collection."
       :group :stdlib.collection)

      (set-global-word-defn
       'empty? (wrap-function-with-arity 1 empty?)
       :stdlib? true
       :doc "( coll -- b ) Returns true if the collection is empty."
       :group :stdlib.collection)

      (set-global-word-defn
       'find (wrap-function-with-arity 2 find)
       :stdlib? true
       :doc "( map key -- kv ) Returns the map entry for `key` in `map`."
       :group :stdlib.collection)

      (set-global-word-defn
       'first (wrap-function-with-arity 1 first)
       :stdlib? true
       :doc "( coll -- x ) Returns the first element in the collection, otherwise nil."
       :group :stdlib.collection)

      (set-global-word-defn
       'flatten (wrap-function-with-arity 1 flatten)
       :stdlib? true
       :doc "( coll -- coll ) Returns the collection with any inner collections flattened."
       :group :stdlib.collection)

      (set-global-word-defn
       'fnext (wrap-function-with-arity 1 fnext)
       :stdlib? true
       :doc "( coll -- coll ) Same as `coll next first`."
       :group :stdlib.collection)

      (set-global-word-defn
       'get (wrap-function-with-arity 2 get)
       :stdlib? true
       :doc "( map key -- val ) Returns the value `val` mapped to `key` within `map`."
       :group :stdlib.collection)

      (set-global-word-defn
       'get-in (wrap-function-with-arity 2 get-in)
       :stdlib? true
       :doc "( map kv -- val ) Returns the value `val` in a nested map of keys `kv` in `map`."
       :group :stdlib.collection)

      (set-global-word-defn
       'idrop (wrap-function-with-arity 2 drop)
       :stdlib? true
       :doc "( n coll -- coll ) Returns a lazy-seq of all but the first `n` items of `coll`."
       :group :stdlib.collection)

      (set-global-word-defn
       'interleave (wrap-function-with-arity 2 interleave)
       :stdlib? true
       :doc "( c1 c2 -- coll ) Returns a lazy-seq of the first item in each coll."
       :group :stdlib.collection)

      (set-global-word-defn
       'interleave3 (wrap-function-with-arity 3 interleave)
       :stdlib? true
       :doc "( c1 c2 c3 -- coll ) 3-arity of interleave"
       :group :stdlib.collection)

      (set-global-word-defn
       'interleave4 (wrap-function-with-arity 4 interleave)
       :stdlib? true
       :doc "( c1 c2 c3 c4 -- coll ) 4-arity of interleave"
       :group :stdlib.collection)

      (set-global-word-defn
       'interpose (wrap-function-with-arity 2 interpose)
       :stdlib? true
       :doc "( sep coll -- coll ) Returns a lazy-seq of `coll` with each element separated by `sep`."
       :group :stdlib.collection)

      (set-global-word-defn
       'into (wrap-function-with-arity 2 into)
       :stdlib? true
       :doc "( to from -- coll) Returns a new `coll` consisting of `to` collection with all the items of `from` collection."
       :group :stdlib.collection)

      (set-global-word-defn
       'keys (wrap-function-with-arity 1 keys)
       :stdlib? true
       :doc "( map -- coll ) Returns a sequence of map keys in `map`."
       :group :stdlib.collection)

      (set-global-word-defn
       'last (wrap-function-with-arity 1 last)
       :stdlib? true
       :doc "( coll -- val ) Returns the last element in `coll`."
       :group :stdlib.collection)

      (set-global-word-defn
       'merge (wrap-function-with-arity 2 merge)
       :stdlib? true
       :doc "( m1 m2 -- m ) Returns a map containing the keyvals of 'm1' and 'm2' (m2>m1)."
       :group :stdlib.collection)

      (set-global-word-defn
       'next (wrap-function-with-arity 1 next)
       :stdlib? true
       :doc "( coll -- coll ) Returns a seq of items after the first"
       :group :stdlib.collection)

      (set-global-word-defn
       'nnext (wrap-function-with-arity 1 nnext)
       :stdlib? true
       :doc "( coll -- coll ) Same as `next next`."
       :group :stdlib.collection)

      (set-global-word-defn
       'not-empty (wrap-function-with-arity 1 not-empty)
       :stdlib? true
       :doc "( coll -- coll ) Returns the collection if it not empty. otherwise nil."
       :group :stdlib.collection)

      (set-global-word-defn
       'nth (wrap-function-with-arity 2 nth)
       :stdlib? true
       :doc "( coll n -- val ) Returns the element at index `n` of `coll`."
       :group :stdlib.collection)

      (set-global-word-defn
       'nthrest (wrap-function-with-arity 2 nthrest)
       :stdlib? true
       :doc "( coll n -- coll ) Returns the rest of the collection after index `n`."
       :group :stdlib.collection)

      (set-global-word-defn
       'pair (wrap-function-with-arity 2 pair)
       :stdlib? true
       :doc "( k v -- kv ) Creates a vector pair from `k` and `v`."
       :group :stdlib.collection)

      (set-global-word-defn
       'partition (wrap-function-with-arity 2 partition)
       :stdlib? true
       :doc "( n coll -- coll ) Returns a lazy-seq of lists of `n` items each"
       :group :stdlib.collection)

      (set-global-word-defn
       'partition-all (wrap-function-with-arity 2 partition-all)
       :stdlib? true
       :doc "( n coll ) Same as `partition`, but will maintain fewer items in the last collection."
       :group :stdlib.collection)

      (set-global-word-defn
       'partition-all3 (wrap-function-with-arity 3 partition-all)
       :stdlib? true
       :doc "( n step coll ) Same as 3-arity `paritition`."
       :group :stdlib.collection)

      (set-global-word-defn
       'partition3 (wrap-function-with-arity 3 partition)
       :stdlib? true
       :doc "( n step coll -- coll ) Returns lazy-seq of lists of `n` at offsets `step`."
       :group :stdlib.collection)

      (set-global-word-defn
       'partition4 (wrap-function-with-arity 4 partition)
       :stdlib? true
       :doc "( n step pad coll -- coll ) Returns a lazy-seq of lists of `n` at offsets `step` padded with `pad`."
       :group :stdlib.collection)

      (set-global-word-defn
       'peek (wrap-function-with-arity 1 peek)
       :stdlib? true
       :doc "( coll -- val ) Peeks into the collection to retrieve a value."
       :group :stdlib.collection)

      (set-global-word-defn
       'pop (wrap-function-with-arity 1 pop)
       :stdlib? true
       :doc "( coll -- coll ) Returns a new collection with a value removed."
       :group :stdlib.collection)

      (set-global-word-defn
       'rand-nth (wrap-function-with-arity 1 rand-nth)
       :stdlib? true
       :doc "( coll -- val ) Return a random element of `coll`."
       :group :stdlib.random)

      (set-global-word-defn
       'random-sample (wrap-function-with-arity 2 random-sample)
       :stdlib? true
       :doc "( prob coll -- coll ) Returns items from coll with random probability `prob` (0.0 - 1.0)."
       :group :stdlib.random)

      (set-global-word-defn
       'range (wrap-function-with-arity 2 range)
       :stdlib? true
       :doc "( start end -- coll ) Returns a lazy-seq of nums from [start end)."
       :group :stdlib.collection)

      (set-global-word-defn
       'replace (wrap-function-with-arity 2 replace)
       :stdlib? true
       :doc "( smap coll -- coll ) Returns a new collection with smap replacements."
       :group :stdlib.collection)

      (set-global-word-defn
       'rest (wrap-function-with-arity 1 rest)
       :stdlib? true
       :doc "( coll -- coll ) Returns a new seq without the first element."
       :group :stdlib.collection)

      (set-global-word-defn
       'reverse (wrap-function-with-arity 1 reverse)
       :stdlib? true
       :doc "( coll -- coll ) Returns a new collection with all elements reversed."
       :group :stdlib.collection)

      (set-global-word-defn
       'second (wrap-function-with-arity 1 second)
       :stdlib? true
       :doc "( coll -- val ) Returns the second element in a collection, or nil otherwise."
       :group :stdlib.collection)

      (set-global-word-defn
       'select-keys (wrap-function-with-arity 2 select-keys)
       :stdlib? true
       :doc "( map keyseq -- map ) Returns a map containing only keys in `keyseq`."
       :group :stdlib.collection)

      (set-global-word-defn
       'seq (wrap-function-with-arity 1 seq)
       :stdlib? true
       :doc "( coll -- seq ) Returns a sequence on the collection."
       :group :stdlib.collection)

      (set-global-word-defn
       'set (wrap-function-with-arity 1 set)
       :stdlib? true
       :doc "( coll -- set ) Returns a set of the distinct elements of coll."
       :group :stdlib.collection)

      (set-global-word-defn
       'shuffle (wrap-function-with-arity 1 shuffle)
       :stdlib? true
       :doc "( coll -- coll ) Return a random permutation of `coll`."
       :group :stdlib.random)

      (set-global-word-defn
       'sort (wrap-function-with-arity 1 sort)
       :stdlib? true
       :doc "( coll -- coll ) Returns a sorted sequence of `coll`."
       :group :stdlib.collection)

      (set-global-word-defn
       'split-at (wrap-function-with-arity 2 split-at)
       :stdlib? true
       :doc "( n coll -- coll ) Returns a vector of `[(n coll take) apply (n coll idrop) apply] ?`."
       :group :stdlib.collection)

      (set-global-word-defn
       'take (wrap-function-with-arity 2 take)
       :stdlib? true
       :doc "( n coll -- coll ) Returns a lazy seq of the first `n` items in `coll`."
       :group :stdlib.collection)

      (set-global-word-defn
       'take-last (wrap-function-with-arity 2 take-last)
       :stdlib? true
       :doc "( n coll -- coll ) Returns a lazy-seq of the last `n` items in `coll`."
       :group :stdlib.collection)

      (set-global-word-defn
       'take-nth (wrap-function-with-arity 2 take-nth)
       :stdlib? true
       :doc "( n coll -- coll ) Returns a lazy-seq of every `n`th item in `coll`."
       :group :stdlib.collection)

      (set-global-word-defn
       'unpair op-unpair
       :stdlib? true
       :doc "( kv -- k v ) Places the two values in a vector pair onto the stack."
       :group :stdlib.collection)

      (set-global-word-defn
       'vals (wrap-function-with-arity 1 vals)
       :stdlib? true
       :doc "( map -- vals ) Returns all of the values in the `map` as a sequence of values."
       :group :stdlib.collection)

      (set-global-word-defn
       'vec (wrap-function-with-arity 1 vec)
       :stdlib? true
       :doc "( coll - vec ) Returns the collection in the form of a vector."
       :group :stdlib.collection)))


