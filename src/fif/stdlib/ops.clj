(ns fif.stdlib.ops
  "Standard Library Word Definitions for common operators

  - Most of the functions listed were taken from the Forth standard library."
  (:refer-clojure :exclude [eval])
  (:require
   [clojure.string :as str]
   [fif.stack :refer :all]
   [fif.def :refer :all]))


(defn op+
  "(n n -- n) Add top two values of stack"
  [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core/+ j i)]
    (-> sm pop-stack pop-stack (push-stack result) dequeue-code)))


(defn op-
  "(n n -- n) Subtract top two values of stack"
  [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core/- j i)]
    (-> sm pop-stack pop-stack (push-stack result) dequeue-code)))


(defn op-plus-1
  [sm]
  (let [[i] (get-stack sm)
        result (inc i)]
    (-> sm pop-stack (push-stack result) dequeue-code)))


(defn op-minus-1
  [sm]
  (let [[i] (get-stack sm)
        result (dec i)]
    (-> sm pop-stack (push-stack result) dequeue-code)))


(defn op*
  "(n n -- n) Multiply top two values of the stack"
  [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core/* j i)]
    (-> sm pop-stack pop-stack (push-stack result) dequeue-code)))


(defn op-div
  "(n n -- n) Divide top two values of the stack"
  [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core// j i)]
    (-> sm pop-stack pop-stack (push-stack result) dequeue-code)))


(defn op-mod
  "(n n -- n) Get the modulo of the top two values"
  [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core/mod j i)]
    (-> sm pop-stack pop-stack (push-stack result) dequeue-code)))


(defn negate
  "(n -- n) Negates the top value"
  [sm]
  (let [[i] (get-stack sm)
        result (clojure.core/- i)]
    (-> sm pop-stack (push-stack result) dequeue-code)))


(defn abs
  "(n -- n) Gets the absolute of the top value"
  [sm]
  (let [[i] (get-stack sm)
        result (if (pos? i) i (clojure.core/- i))]
    (-> sm pop-stack (push-stack result) dequeue-code)))


(defn op-max
  "(n n -- n) Gets the max value between the top two values"
  [sm]
  (let [[i j] (get-stack sm)
        result (max j i)]
    (-> sm pop-stack pop-stack (push-stack result) dequeue-code)))


(defn op-min
  "(n n -- n) Gets teh min value between the top two values"
  [sm]
  (let [[i j] (get-stack sm)
        result (min j i)]
    (-> sm pop-stack pop-stack (push-stack result) dequeue-code)))


(defn dup [sm]
  (let [top (-> sm get-stack peek)]
    (-> sm (push-stack top) dequeue-code)))


(defn dot [sm]
  (let [top (-> sm get-stack peek)]
    (print top)
    (-> sm pop-stack dequeue-code)))


(defn carriage-return [sm]
  (print "\n")
  (-> sm dequeue-code))


(defn dot-stack [sm]
  (let [stack (get-stack sm)
        result (str "<" (count stack) "> ")]
    (print (str "<" (count stack) "> "))
    (prn stack)
    (-> sm dequeue-code)))


(defn push-return [sm]
  (let [[i] (get-stack sm)]
    (-> sm pop-stack (push-ret i) dequeue-code)))


(defn pop-return [sm]
  (let [[i] (get-ret sm)]
    (-> sm pop-ret (push-stack i) dequeue-code)))


(defn swap [sm]
  (let [[i j] (get-stack sm)]
    (-> sm pop-stack pop-stack (push-stack i) (push-stack j) dequeue-code)))


(defn rot [sm]
  (let [[k j i] (get-stack sm)]
    (-> sm pop-stack pop-stack pop-stack
        (push-stack j) (push-stack k) (push-stack i) dequeue-code)))


(defn op-drop [sm]
  (-> sm pop-stack dequeue-code))


(defn nip [sm]
  (let [[i j] (get-stack sm)]
    (-> sm pop-stack pop-stack (push-stack i) dequeue-code)))


(defn tuck [sm]
  (let [[i j] (get-stack sm)]
    (-> sm pop-stack pop-stack
        (push-stack i) (push-stack j) (push-stack i) dequeue-code)))


(defn over [sm]
  (let [[i j] (get-stack sm)]
    (-> sm (push-stack j) dequeue-code)))


(defn roll
  "(v v --) *move* the item at that position to the top"
  [sm]
  (let [stack (get-stack sm)
        pos (peek stack)
        item (nth stack pos)
        new-stack (nthrest stack)]))


(defn op-<
  [sm]
  (let [[i j] (get-stack sm)
        result (clojure.core/< j i)]
   (-> sm
       pop-stack pop-stack (push-stack result) dequeue-code)))


(defstack-func-2 op-<= <=)
(defstack-func-2 op-= =)
(defstack-func-2 op-not= not=)
(defstack-func-2 op-> >)
(defstack-func-2 op->= >=)


(defn import-stdlib-ops [sm]
  (-> sm

      ;; Arithmetic
      (set-word '+ op+)
      (set-word '- op-)
      (set-word 'inc op-plus-1)
      (set-word 'dec op-minus-1)
      (set-word '* op*)
      (set-word '/ op-div)
      (set-word 'mod op-mod)
      (set-word 'negate negate)
      (set-word 'abs abs)
      (set-word 'max op-max)
      (set-word 'min op-min)
      (set-word 'rem (wrap-function-with-arity 2 rem))
      (set-word 'quot (wrap-function-with-arity 2 quot))

      ;; Bitwise
      (set-word 'bit-and (wrap-function-with-arity 2 bit-and))
      (set-word 'bit-or (wrap-function-with-arity 2 bit-or))
      (set-word 'bit-xor (wrap-function-with-arity 2 bit-xor))
      (set-word 'bit-not (wrap-function-with-arity 2 bit-not))
      (set-word 'bit-flip (wrap-function-with-arity 2 bit-flip))
      (set-word 'bit-shift-right (wrap-function-with-arity 2 bit-shift-right))
      (set-word 'bit-shift-left (wrap-function-with-arity 2 bit-shift-left))
      (set-word 'bit-and-not (wrap-function-with-arity 2 bit-and-not))
      (set-word 'bit-clear (wrap-function-with-arity 2 bit-clear))
      (set-word 'bit-test (wrap-function-with-arity 2 bit-test))
      (set-word 'unsigned-bit-shift-right (wrap-function-with-arity 2 unsigned-bit-shift-right))
      (set-word 'byte (wrap-function-with-arity 1 byte))

      ;; Forth-based
      (set-word 'dup dup)
      (set-word '. dot)
      (set-word 'cr carriage-return)
      (set-word '.s dot-stack)
      (set-word '>r push-return)
      (set-word 'r> pop-return)
      (set-word 'swap swap)
      (set-word 'rot rot)
      (set-word 'drop op-drop)
      (set-word 'nip nip)
      (set-word 'tuck tuck)
      (set-word 'over over)
      (set-word 'roll roll)

      ;; Comparison Operators
      (set-word '< op-<)
      (set-word '<= op-<=)
      (set-word '= op-=)
      (set-word 'not= op-not=)
      (set-word '> op->)
      (set-word '>= op->=)
      (set-word 'compare (wrap-function-with-arity 2 compare))
      (set-word 'and (wrap-function-with-arity 2 #(and %1 %2)))
      (set-word 'or (wrap-function-with-arity 2 #(or %1 %2)))
      (set-word 'not (wrap-function-with-arity 1 not))

      ;; Type Checking
      (set-word 'map? (wrap-function-with-arity 1 map?))
      (set-word 'vector? (wrap-function-with-arity 1 vector?))
      (set-word 'set? (wrap-function-with-arity 1 set?))
      (set-word 'list? (wrap-function-with-arity 1 list?))
      (set-word 'string? (wrap-function-with-arity 1 string?))
      (set-word 'boolean? (wrap-function-with-arity 1 boolean?))
      (set-word 'bytes? (wrap-function-with-arity 1 bytes?))
      (set-word 'keyword? (wrap-function-with-arity 1 keyword?))
      (set-word 'int? (wrap-function-with-arity 1 int?))
      (set-word 'nat-int? (wrap-function-with-arity 1 nat-int?))
      (set-word 'neg-int? (wrap-function-with-arity 1 neg-int?))
      (set-word 'pos-int? (wrap-function-with-arity 1 pos-int?))
      (set-word 'number? (wrap-function-with-arity 1 number?))
      (set-word 'ratio? (wrap-function-with-arity 1 ratio?))
      (set-word 'rational? (wrap-function-with-arity 1 rational?))
      (set-word 'integer? (wrap-function-with-arity 1 integer?))
      (set-word 'decimal? (wrap-function-with-arity 1 decimal?))
      (set-word 'symbol? (wrap-function-with-arity 1 symbol?))
      (set-word 'float? (wrap-function-with-arity 1 float?))
      (set-word 'double? (wrap-function-with-arity 1 double?))
      (set-word 'zero? (wrap-function-with-arity 1 zero?))
      (set-word 'nil? (wrap-function-with-arity 1 nil?))
      (set-word 'some? (wrap-function-with-arity 1 some?))
      (set-word 'true? (wrap-function-with-arity 1 true?))
      (set-word 'false? (wrap-function-with-arity 1 false?))
      (set-word 'inst? (wrap-function-with-arity 1 inst?))
      (set-word 'uri? (wrap-function-with-arity 1 uri?))
      (set-word 'uuid? (wrap-function-with-arity 1 uuid?))
      (set-word 'associative? (wrap-function-with-arity 1 associative?))
      (set-word 'coll? (wrap-function-with-arity 1 coll?))
      (set-word 'sequential? (wrap-function-with-arity 1 sequential?))
      (set-word 'seq? (wrap-function-with-arity 1 seq?))
      (set-word 'indexed? (wrap-function-with-arity 1 indexed?))
      (set-word 'seqable? (wrap-function-with-arity 1 seqable?))
      (set-word 'any? (wrap-function-with-arity 1 any?))

      ;; Other Stuff
      (set-word 'deref (wrap-function-with-arity 1 deref))
      (set-word 'print (wrap-procedure-with-arity 1 print))
      (set-word 'println (wrap-procedure-with-arity 1 println))
      (set-word 'pr (wrap-procedure-with-arity 1 pr))
      (set-word 'prn (wrap-procedure-with-arity 1 prn))
      (set-word 'newline (wrap-procedure-with-arity 1 newline))
      (set-word 'count (wrap-function-with-arity 1 count))
      (set-word 'subvec (wrap-function-with-arity 3 subvec))
      (set-word 'subs (wrap-function-with-arity 3 subs))
      (set-word 'class (wrap-function-with-arity 1 class))
      (set-word 'str (wrap-function-with-arity 2 str))
      (set-word 'rand (wrap-function-with-arity 1 rand))
      (set-word 'randn (wrap-function-with-arity 2 rand))
      (set-word 'str/index-of (wrap-function-with-arity 2 str/index-of))
      (set-word 'str/last-index-of (wrap-function-with-arity 2 str/last-index-of))
      (set-word 'str/split-lines (wrap-function-with-arity 1 str/split-lines))
      (set-word 'str/join (wrap-function-with-arity 2 str/join))
      (set-word 'str/escape (wrap-function-with-arity 2 str/escape))
      (set-word 'str/split (wrap-function-with-arity 2 str/split))
      (set-word 'str/replace (wrap-function-with-arity 3 str/replace))
      (set-word 'str/replace-first (wrap-function-with-arity 3 str/replace-first))
      (set-word 'str/capitalize (wrap-function-with-arity 1 str/capitalize))
      (set-word 'str/lower-case (wrap-function-with-arity 1 str/lower-case))
      (set-word 'str/upper-case (wrap-function-with-arity 1 str/upper-case))
      (set-word 'str/trim (wrap-function-with-arity 1 str/trim))
      (set-word 'str/trim-newline (wrap-function-with-arity 1 str/trim-newline))
      (set-word 'str/triml (wrap-function-with-arity 1 str/triml))
      (set-word 'str/trimr (wrap-function-with-arity 1 str/trimr))
      (set-word 'str/blank? (wrap-function-with-arity 1 str/blank?))
      (set-word 'str/starts-with? (wrap-function-with-arity 2 str/starts-with?))
      (set-word 'str/ends-with? (wrap-function-with-arity 2 str/ends-with?))
      (set-word 'str/includes? (wrap-function-with-arity 2 str/includes?))

      ;; Regex
      (set-word 're-find (wrap-function-with-arity 2 re-find))
      (set-word 're-find-match (wrap-function-with-arity 1 re-find))
      (set-word 're-seq (wrap-function-with-arity 2 re-seq))
      (set-word 're-matches (wrap-function-with-arity 2 re-matches))
      (set-word 're-pattern (wrap-function-with-arity 1 re-pattern))
      (set-word 're-matcher (wrap-function-with-arity 2 re-matcher))
      (set-word 're-groups (wrap-function-with-arity 1 re-groups))))
