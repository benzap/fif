(ns fif.stdlib.ops
  "Standard Library Word Definitions for common operators

  - Most of the functions listed were taken from the Forth standard library."
  (:require
   [clojure.string :as str]
   [fif.stack-machine :as stack]
   [fif.def :as def
    :refer [wrap-function-with-arity
            wrap-procedure-with-arity]
    :include-macros true]))


(defn op+
  "(n n -- n) Add top two values of stack"
  [sm]
  (let [[i j] (stack/get-stack sm)
        result (clojure.core/+ j i)]
    (-> sm stack/pop-stack stack/pop-stack (stack/push-stack result) stack/dequeue-code)))


(defn op-
  "(n n -- n) Subtract top two values of stack"
  [sm]
  (let [[i j] (stack/get-stack sm)
        result (clojure.core/- j i)]
    (-> sm stack/pop-stack stack/pop-stack (stack/push-stack result) stack/dequeue-code)))


(defn op-plus-1
  [sm]
  (let [[i] (stack/get-stack sm)
        result (inc i)]
    (-> sm stack/pop-stack (stack/push-stack result) stack/dequeue-code)))


(defn op-minus-1
  [sm]
  (let [[i] (stack/get-stack sm)
        result (dec i)]
    (-> sm stack/pop-stack (stack/push-stack result) stack/dequeue-code)))


(defn op*
  "(n n -- n) Multiply top two values of the stack"
  [sm]
  (let [[i j] (stack/get-stack sm)
        result (clojure.core/* j i)]
    (-> sm stack/pop-stack stack/pop-stack (stack/push-stack result) stack/dequeue-code)))


(defn op-div
  "(n n -- n) Divide top two values of the stack"
  [sm]
  (let [[i j] (stack/get-stack sm)
        result (clojure.core// j i)]
    (-> sm stack/pop-stack stack/pop-stack (stack/push-stack result) stack/dequeue-code)))


(defn op-mod
  "(n n -- n) Get the modulo of the top two values"
  [sm]
  (let [[i j] (stack/get-stack sm)
        result (clojure.core/mod j i)]
    (-> sm stack/pop-stack stack/pop-stack (stack/push-stack result) stack/dequeue-code)))


(defn negate
  "(n -- n) Negates the top value"
  [sm]
  (let [[i] (stack/get-stack sm)
        result (clojure.core/- i)]
    (-> sm stack/pop-stack (stack/push-stack result) stack/dequeue-code)))


(defn abs
  "(n -- n) Gets the absolute of the top value"
  [sm]
  (let [[i] (stack/get-stack sm)
        result (if (pos? i) i (clojure.core/- i))]
    (-> sm stack/pop-stack (stack/push-stack result) stack/dequeue-code)))


(defn op-max
  "(n n -- n) Gets the max value between the top two values"
  [sm]
  (let [[i j] (stack/get-stack sm)
        result (max j i)]
    (-> sm stack/pop-stack stack/pop-stack (stack/push-stack result) stack/dequeue-code)))


(defn op-min
  "(n n -- n) Gets teh min value between the top two values"
  [sm]
  (let [[i j] (stack/get-stack sm)
        result (min j i)]
    (-> sm stack/pop-stack stack/pop-stack (stack/push-stack result) stack/dequeue-code)))


(defn dup [sm]
  (let [top (-> sm stack/get-stack peek)]
    (-> sm (stack/push-stack top) stack/dequeue-code)))


(defn dot [sm]
  (let [top (-> sm stack/get-stack peek)]
    (print top)
    (-> sm stack/pop-stack stack/dequeue-code)))


(defn carriage-return [sm]
  (print "\n")
  (-> sm stack/dequeue-code))


(defn dot-stack [sm]
  (let [stack (stack/get-stack sm)
        result (str "<" (count stack) "> ")]
    (print (str "<" (count stack) "> "))
    (prn stack)
    (-> sm stack/dequeue-code)))


(defn push-return [sm]
  (let [[i] (stack/get-stack sm)]
    (-> sm stack/pop-stack (stack/push-ret i) stack/dequeue-code)))


(defn pop-return [sm]
  (let [[i] (stack/get-ret sm)]
    (-> sm stack/pop-ret (stack/push-stack i) stack/dequeue-code)))


(defn swap [sm]
  (let [[i j] (stack/get-stack sm)]
    (-> sm stack/pop-stack stack/pop-stack (stack/push-stack i) (stack/push-stack j) stack/dequeue-code)))


(defn rot [sm]
  (let [[k j i] (stack/get-stack sm)]
    (-> sm stack/pop-stack stack/pop-stack stack/pop-stack
        (stack/push-stack j) (stack/push-stack k) (stack/push-stack i) stack/dequeue-code)))


(defn op-drop [sm]
  (-> sm stack/pop-stack stack/dequeue-code))


(defn nip [sm]
  (let [[i j] (stack/get-stack sm)]
    (-> sm stack/pop-stack stack/pop-stack (stack/push-stack i) stack/dequeue-code)))


(defn tuck [sm]
  (let [[i j] (stack/get-stack sm)]
    (-> sm stack/pop-stack stack/pop-stack
        (stack/push-stack i) (stack/push-stack j) (stack/push-stack i) stack/dequeue-code)))


(defn over [sm]
  (let [[i j] (stack/get-stack sm)]
    (-> sm (stack/push-stack j) stack/dequeue-code)))


(defn roll
  "(v v --) *move* the item at that position to the top"
  [sm]
  (let [stack (stack/get-stack sm)
        pos (peek stack)
        item (nth stack pos)
        new-stack (nthrest stack 2)]))


(defn op-<
  [sm]
  (let [[i j] (stack/get-stack sm)
        result (clojure.core/< j i)]
   (-> sm
       stack/pop-stack stack/pop-stack
       (stack/push-stack result) stack/dequeue-code)))


(def/defstack-func-2 op-<= <=)
(def/defstack-func-2 op-= =)
(def/defstack-func-2 op-not= not=)
(def/defstack-func-2 op-> >)
(def/defstack-func-2 op->= >=)


(defn import-stdlib-ops [sm]
  (-> sm

      ;; Arithmetic
      (stack/set-word '* op*)
      (stack/set-word '+ op+)
      (stack/set-word '- op-)
      (stack/set-word '/ op-div)
      (stack/set-word 'abs abs)
      (stack/set-word 'dec op-minus-1)
      (stack/set-word 'inc op-plus-1)
      (stack/set-word 'max op-max)
      (stack/set-word 'min op-min)
      (stack/set-word 'mod op-mod)
      (stack/set-word 'negate negate)
      (stack/set-word 'quot (wrap-function-with-arity 2 quot))
      (stack/set-word 'rem (wrap-function-with-arity 2 rem))

      ;; Bitwise
      (stack/set-word 'bit-and (wrap-function-with-arity 2 bit-and))
      (stack/set-word 'bit-and-not (wrap-function-with-arity 2 bit-and-not))
      (stack/set-word 'bit-clear (wrap-function-with-arity 2 bit-clear))
      (stack/set-word 'bit-flip (wrap-function-with-arity 2 bit-flip))
      (stack/set-word 'bit-not (wrap-function-with-arity 2 bit-not))
      (stack/set-word 'bit-or (wrap-function-with-arity 2 bit-or))
      (stack/set-word 'bit-shift-left (wrap-function-with-arity 2 bit-shift-left))
      (stack/set-word 'bit-shift-right (wrap-function-with-arity 2 bit-shift-right))
      (stack/set-word 'bit-test (wrap-function-with-arity 2 bit-test))
      (stack/set-word 'bit-xor (wrap-function-with-arity 2 bit-xor))
      (stack/set-word 'byte (wrap-function-with-arity 1 byte))
      (stack/set-word 'unsigned-bit-shift-right (wrap-function-with-arity 2 unsigned-bit-shift-right))

      ;; Forth-based
      (stack/set-word '. dot)
      (stack/set-word '.s dot-stack)
      (stack/set-word '<> swap)
      (stack/set-word '>r push-return)
      (stack/set-word 'cr carriage-return)
      (stack/set-word 'drop op-drop)
      (stack/set-word 'dup dup)
      (stack/set-word 'nip nip)
      (stack/set-word 'over over)
      (stack/set-word 'r> pop-return)
      (stack/set-word 'roll roll)
      (stack/set-word 'rot rot)
      (stack/set-word 'swap swap)
      (stack/set-word 'tuck tuck)

      ;; Comparison Operators
      (stack/set-word '< op-<)
      (stack/set-word '<= op-<=)
      (stack/set-word '= op-=)
      (stack/set-word '> op->)
      (stack/set-word '>= op->=)
      (stack/set-word 'and (wrap-function-with-arity 2 #(and %1 %2)))
      (stack/set-word 'compare (wrap-function-with-arity 2 compare))
      (stack/set-word 'not (wrap-function-with-arity 1 not))
      (stack/set-word 'not= op-not=)
      (stack/set-word 'or (wrap-function-with-arity 2 #(or %1 %2)))

      ;; CLJ Type Checking
      #?(:clj
         (stack/set-word 'bytes? (wrap-function-with-arity 1 bytes?)))
      #?(:clj
         (stack/set-word 'ratio? (wrap-function-with-arity 1 ratio?)))
      #?(:clj
         (stack/set-word 'rational? (wrap-function-with-arity 1 rational?)))
      #?(:clj
         (stack/set-word 'decimal? (wrap-function-with-arity 1 decimal?)))

      ;; Type Checking
      (stack/set-word 'any? (wrap-function-with-arity 1 any?))
      (stack/set-word 'associative? (wrap-function-with-arity 1 associative?))
      (stack/set-word 'boolean? (wrap-function-with-arity 1 boolean?))
      (stack/set-word 'coll? (wrap-function-with-arity 1 coll?))
      (stack/set-word 'double? (wrap-function-with-arity 1 double?))
      (stack/set-word 'even? (wrap-function-with-arity 1 even?))
      (stack/set-word 'false? (wrap-function-with-arity 1 false?))
      (stack/set-word 'float? (wrap-function-with-arity 1 float?))
      (stack/set-word 'indexed? (wrap-function-with-arity 1 indexed?))
      (stack/set-word 'inst? (wrap-function-with-arity 1 inst?))
      (stack/set-word 'int? (wrap-function-with-arity 1 int?))
      (stack/set-word 'integer? (wrap-function-with-arity 1 integer?))
      (stack/set-word 'keyword? (wrap-function-with-arity 1 keyword?))
      (stack/set-word 'list? (wrap-function-with-arity 1 list?))
      (stack/set-word 'map? (wrap-function-with-arity 1 map?))
      (stack/set-word 'nat-int? (wrap-function-with-arity 1 nat-int?))
      (stack/set-word 'neg-int? (wrap-function-with-arity 1 neg-int?))
      (stack/set-word 'nil? (wrap-function-with-arity 1 nil?))
      (stack/set-word 'number? (wrap-function-with-arity 1 number?))
      (stack/set-word 'odd? (wrap-function-with-arity 1 odd?))
      (stack/set-word 'pos-int? (wrap-function-with-arity 1 pos-int?))
      (stack/set-word 'seq? (wrap-function-with-arity 1 seq?))
      (stack/set-word 'seqable? (wrap-function-with-arity 1 seqable?))
      (stack/set-word 'sequential? (wrap-function-with-arity 1 sequential?))
      (stack/set-word 'set? (wrap-function-with-arity 1 set?))
      (stack/set-word 'some? (wrap-function-with-arity 1 some?))
      (stack/set-word 'string? (wrap-function-with-arity 1 string?))
      (stack/set-word 'symbol? (wrap-function-with-arity 1 symbol?))
      (stack/set-word 'true? (wrap-function-with-arity 1 true?))
      (stack/set-word 'uri? (wrap-function-with-arity 1 uri?))
      (stack/set-word 'uuid? (wrap-function-with-arity 1 uuid?))
      (stack/set-word 'vector? (wrap-function-with-arity 1 vector?))
      (stack/set-word 'zero? (wrap-function-with-arity 1 zero?))

      ;; Other Stuff
      (stack/set-word 'count (wrap-function-with-arity 1 count))
      (stack/set-word 'deref (wrap-function-with-arity 1 deref))
      (stack/set-word 'newline (wrap-procedure-with-arity 1 newline))
      (stack/set-word 'pr (wrap-procedure-with-arity 1 pr))
      (stack/set-word 'print (wrap-procedure-with-arity 1 print))
      (stack/set-word 'println (wrap-procedure-with-arity 1 println))
      (stack/set-word 'prn (wrap-procedure-with-arity 1 prn))
      (stack/set-word 'rand (wrap-function-with-arity 1 rand))
      (stack/set-word 'randn (wrap-function-with-arity 2 rand))
      (stack/set-word 'str (wrap-function-with-arity 2 str))
      (stack/set-word 'str/blank? (wrap-function-with-arity 1 str/blank?))
      (stack/set-word 'str/capitalize (wrap-function-with-arity 1 str/capitalize))
      (stack/set-word 'str/ends-with? (wrap-function-with-arity 2 str/ends-with?))
      (stack/set-word 'str/escape (wrap-function-with-arity 2 str/escape))
      (stack/set-word 'str/includes? (wrap-function-with-arity 2 str/includes?))
      (stack/set-word 'str/index-of (wrap-function-with-arity 2 str/index-of))
      (stack/set-word 'str/join (wrap-function-with-arity 2 str/join))
      (stack/set-word 'str/last-index-of (wrap-function-with-arity 2 str/last-index-of))
      (stack/set-word 'str/lower-case (wrap-function-with-arity 1 str/lower-case))
      (stack/set-word 'str/replace (wrap-function-with-arity 3 str/replace))
      (stack/set-word 'str/replace-first (wrap-function-with-arity 3 str/replace-first))
      (stack/set-word 'str/split (wrap-function-with-arity 2 str/split))
      (stack/set-word 'str/split-lines (wrap-function-with-arity 1 str/split-lines))
      (stack/set-word 'str/starts-with? (wrap-function-with-arity 2 str/starts-with?))
      (stack/set-word 'str/trim (wrap-function-with-arity 1 str/trim))
      (stack/set-word 'str/trim-newline (wrap-function-with-arity 1 str/trim-newline))
      (stack/set-word 'str/triml (wrap-function-with-arity 1 str/triml))
      (stack/set-word 'str/trimr (wrap-function-with-arity 1 str/trimr))
      (stack/set-word 'str/upper-case (wrap-function-with-arity 1 str/upper-case))
      (stack/set-word 'subs (wrap-function-with-arity 3 subs))
      (stack/set-word 'subvec (wrap-function-with-arity 3 subvec))

      ;; Regex
      (stack/set-word 're-find (wrap-function-with-arity 2 re-find))
      (stack/set-word 're-find-match (wrap-function-with-arity 1 re-find))
      (stack/set-word 're-seq (wrap-function-with-arity 2 re-seq))
      (stack/set-word 're-matches (wrap-function-with-arity 2 re-matches))
      (stack/set-word 're-pattern (wrap-function-with-arity 1 re-pattern))
      (stack/set-word 'regex (wrap-function-with-arity 1 re-pattern))

      ;; CLJ Specific
      #?(:clj
         (stack/set-word 'class (wrap-function-with-arity 1 class)))
      #?(:clj
         (stack/set-word 're-matcher (wrap-function-with-arity 2 re-matcher)))
      #?(:clj
         (stack/set-word 're-groups (wrap-function-with-arity 1 re-groups)))

      ))

