(ns fif.stdlib.ops
  "Standard Library Word Definitions for common operators

  - Most of the functions listed were taken from the Forth standard library."
  (:require
   [clojure.string :as str]
   [fif.stack-machine :as stack]
   [fif.stack-machine.words :as words :refer [set-word-defn]]
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
      (set-word-defn '* op* :stdlib? true :doc "(n n -- n) Multiply the top two values on the stack")
      (set-word-defn '+ op+ :stdlib? true :doc "(n n -- n) Add the top two values on the stack")
      (set-word-defn '- op-)
      (set-word-defn '/ op-div)
      (set-word-defn 'abs abs)
      (set-word-defn 'dec op-minus-1)
      (set-word-defn 'inc op-plus-1)
      (set-word-defn 'max op-max)
      (set-word-defn 'min op-min)
      (set-word-defn 'mod op-mod)
      (set-word-defn 'negate negate)
      (set-word-defn 'quot (wrap-function-with-arity 2 quot))
      (set-word-defn 'rem (wrap-function-with-arity 2 rem))

      ;; Bitwise
      (set-word-defn 'bit-and (wrap-function-with-arity 2 bit-and))
      (set-word-defn 'bit-and-not (wrap-function-with-arity 2 bit-and-not))
      (set-word-defn 'bit-clear (wrap-function-with-arity 2 bit-clear))
      (set-word-defn 'bit-flip (wrap-function-with-arity 2 bit-flip))
      (set-word-defn 'bit-not (wrap-function-with-arity 1 bit-not))
      (set-word-defn 'bit-or (wrap-function-with-arity 2 bit-or))
      (set-word-defn 'bit-shift-left (wrap-function-with-arity 2 bit-shift-left))
      (set-word-defn 'bit-shift-right (wrap-function-with-arity 2 bit-shift-right))
      (set-word-defn 'bit-test (wrap-function-with-arity 2 bit-test))
      (set-word-defn 'bit-xor (wrap-function-with-arity 2 bit-xor))
      (set-word-defn 'byte (wrap-function-with-arity 1 byte))
      (set-word-defn 'unsigned-bit-shift-right (wrap-function-with-arity 2 unsigned-bit-shift-right))

      ;; Forth-based
      (set-word-defn '. dot)
      (set-word-defn '.s dot-stack)
      (set-word-defn '<> swap)
      (set-word-defn '>r push-return)
      (set-word-defn 'cr carriage-return)
      (set-word-defn 'drop op-drop)
      (set-word-defn 'dup dup)
      (set-word-defn 'nip nip)
      (set-word-defn 'over over)
      (set-word-defn 'r> pop-return)
      (set-word-defn 'roll roll)
      (set-word-defn 'rot rot)
      (set-word-defn 'swap swap)
      (set-word-defn 'tuck tuck)

      ;; Comparison Operators
      (set-word-defn '< op-<)
      (set-word-defn '<= op-<=)
      (set-word-defn '= op-=)
      (set-word-defn '> op->)
      (set-word-defn '>= op->=)
      (set-word-defn 'and (wrap-function-with-arity 2 #(and %1 %2)))
      (set-word-defn 'compare (wrap-function-with-arity 2 compare))
      (set-word-defn 'not (wrap-function-with-arity 1 not))
      (set-word-defn 'not= op-not=)
      (set-word-defn 'or (wrap-function-with-arity 2 #(or %1 %2)))

      ;; CLJ Type Checking
      #?(:clj
         (set-word-defn 'bytes? (wrap-function-with-arity 1 bytes?)))
      #?(:clj
         (set-word-defn 'ratio? (wrap-function-with-arity 1 ratio?)))
      #?(:clj
         (set-word-defn 'rational? (wrap-function-with-arity 1 rational?)))
      #?(:clj
         (set-word-defn 'decimal? (wrap-function-with-arity 1 decimal?)))

      ;; Type Checking
      (set-word-defn 'any? (wrap-function-with-arity 1 any?))
      (set-word-defn 'associative? (wrap-function-with-arity 1 associative?))
      (set-word-defn 'boolean? (wrap-function-with-arity 1 boolean?))
      (set-word-defn 'coll? (wrap-function-with-arity 1 coll?))
      (set-word-defn 'double? (wrap-function-with-arity 1 double?))
      (set-word-defn 'even? (wrap-function-with-arity 1 even?))
      (set-word-defn 'false? (wrap-function-with-arity 1 false?))
      (set-word-defn 'float? (wrap-function-with-arity 1 float?))
      (set-word-defn 'indexed? (wrap-function-with-arity 1 indexed?))
      (set-word-defn 'inst? (wrap-function-with-arity 1 inst?))
      (set-word-defn 'int? (wrap-function-with-arity 1 int?))
      (set-word-defn 'integer? (wrap-function-with-arity 1 integer?))
      (set-word-defn 'keyword? (wrap-function-with-arity 1 keyword?))
      (set-word-defn 'list? (wrap-function-with-arity 1 list?))
      (set-word-defn 'map? (wrap-function-with-arity 1 map?))
      (set-word-defn 'nat-int? (wrap-function-with-arity 1 nat-int?))
      (set-word-defn 'neg-int? (wrap-function-with-arity 1 neg-int?))
      (set-word-defn 'nil? (wrap-function-with-arity 1 nil?))
      (set-word-defn 'number? (wrap-function-with-arity 1 number?))
      (set-word-defn 'odd? (wrap-function-with-arity 1 odd?))
      (set-word-defn 'pos-int? (wrap-function-with-arity 1 pos-int?))
      (set-word-defn 'seq? (wrap-function-with-arity 1 seq?))
      (set-word-defn 'seqable? (wrap-function-with-arity 1 seqable?))
      (set-word-defn 'sequential? (wrap-function-with-arity 1 sequential?))
      (set-word-defn 'set? (wrap-function-with-arity 1 set?))
      (set-word-defn 'some? (wrap-function-with-arity 1 some?))
      (set-word-defn 'string? (wrap-function-with-arity 1 string?))
      (set-word-defn 'symbol? (wrap-function-with-arity 1 symbol?))
      (set-word-defn 'true? (wrap-function-with-arity 1 true?))
      (set-word-defn 'uri? (wrap-function-with-arity 1 uri?))
      (set-word-defn 'uuid? (wrap-function-with-arity 1 uuid?))
      (set-word-defn 'vector? (wrap-function-with-arity 1 vector?))
      (set-word-defn 'zero? (wrap-function-with-arity 1 zero?))

      ;; Other Stuff
      (set-word-defn 'count (wrap-function-with-arity 1 count))
      (set-word-defn 'deref (wrap-function-with-arity 1 deref))
      (set-word-defn 'newline (wrap-procedure-with-arity 0 newline))
      (set-word-defn 'pr (wrap-procedure-with-arity 1 pr))
      (set-word-defn 'print (wrap-procedure-with-arity 1 print))
      (set-word-defn 'println (wrap-procedure-with-arity 1 println))
      (set-word-defn 'prn (wrap-procedure-with-arity 1 prn))
      (set-word-defn 'rand (wrap-function-with-arity 1 rand))
      (set-word-defn 'randn (wrap-function-with-arity 2 rand))
      (set-word-defn 'str (wrap-function-with-arity 2 str))
      (set-word-defn 'str/blank? (wrap-function-with-arity 1 str/blank?))
      (set-word-defn 'str/capitalize (wrap-function-with-arity 1 str/capitalize))
      (set-word-defn 'str/ends-with? (wrap-function-with-arity 2 str/ends-with?))
      (set-word-defn 'str/escape (wrap-function-with-arity 2 str/escape))
      (set-word-defn 'str/includes? (wrap-function-with-arity 2 str/includes?))
      (set-word-defn 'str/index-of (wrap-function-with-arity 2 str/index-of))
      (set-word-defn 'str/join (wrap-function-with-arity 2 str/join))
      (set-word-defn 'str/last-index-of (wrap-function-with-arity 2 str/last-index-of))
      (set-word-defn 'str/lower-case (wrap-function-with-arity 1 str/lower-case))
      (set-word-defn 'str/replace (wrap-function-with-arity 3 str/replace))
      (set-word-defn 'str/replace-first (wrap-function-with-arity 3 str/replace-first))
      (set-word-defn 'str/split (wrap-function-with-arity 2 str/split))
      (set-word-defn 'str/split-lines (wrap-function-with-arity 1 str/split-lines))
      (set-word-defn 'str/starts-with? (wrap-function-with-arity 2 str/starts-with?))
      (set-word-defn 'str/trim (wrap-function-with-arity 1 str/trim))
      (set-word-defn 'str/trim-newline (wrap-function-with-arity 1 str/trim-newline))
      (set-word-defn 'str/triml (wrap-function-with-arity 1 str/triml))
      (set-word-defn 'str/trimr (wrap-function-with-arity 1 str/trimr))
      (set-word-defn 'str/upper-case (wrap-function-with-arity 1 str/upper-case))
      (set-word-defn 'subs (wrap-function-with-arity 3 subs))
      (set-word-defn 'subvec (wrap-function-with-arity 3 subvec))

      ;; Regex
      (set-word-defn 're-find (wrap-function-with-arity 2 re-find))
      (set-word-defn 're-find-match (wrap-function-with-arity 1 re-find))
      (set-word-defn 're-seq (wrap-function-with-arity 2 re-seq))
      (set-word-defn 're-matches (wrap-function-with-arity 2 re-matches))
      (set-word-defn 're-pattern (wrap-function-with-arity 1 re-pattern))
      (set-word-defn 'regex (wrap-function-with-arity 1 re-pattern))

      ;; CLJ Specific
      #?(:clj
         (set-word-defn 'class (wrap-function-with-arity 1 class)))
      #?(:clj
         (set-word-defn 're-matcher (wrap-function-with-arity 2 re-matcher)))
      #?(:clj
         (set-word-defn 're-groups (wrap-function-with-arity 1 re-groups)))

      ))

