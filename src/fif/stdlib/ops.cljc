(ns fif.stdlib.ops
  "Standard Library Word Definitions for common operators

  - Most of the functions listed were taken from the Forth standard library."
  (:require
   [clojure.string :as str]
   [fif.stack-machine :as stack]
   [fif.stack-machine.words :as words :refer [set-global-word-defn]]
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

      (set-global-word-defn
       '* op*
       :stdlib? true
       :doc "(n n -- n) Multiply the top two values on the stack"
       :group :stdlib)

      (set-global-word-defn
       '+ op+
       :stdlib? true
       :doc "(n n -- n) Add the top two values on the stack"
       :group :stdlib)

      (set-global-word-defn
       '- op-
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       '/ op-div
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'abs abs
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'dec op-minus-1
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'inc op-plus-1
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'max op-max
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'min op-min
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'mod op-mod
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'negate negate
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'quot (wrap-function-with-arity 2 quot)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'rem (wrap-function-with-arity 2 rem)
       :stdlib? true
       :doc ""
       :group :stdlib)

      ;; Bitwise

      (set-global-word-defn
       'bit-and (wrap-function-with-arity 2 bit-and)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'bit-and-not (wrap-function-with-arity 2 bit-and-not)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'bit-clear (wrap-function-with-arity 2 bit-clear)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'bit-flip (wrap-function-with-arity 2 bit-flip)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'bit-not (wrap-function-with-arity 1 bit-not)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'bit-or (wrap-function-with-arity 2 bit-or)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'bit-shift-left (wrap-function-with-arity 2 bit-shift-left)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'bit-shift-right (wrap-function-with-arity 2 bit-shift-right)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'bit-test (wrap-function-with-arity 2 bit-test)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'bit-xor (wrap-function-with-arity 2 bit-xor)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'byte (wrap-function-with-arity 1 byte)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'unsigned-bit-shift-right (wrap-function-with-arity 2 unsigned-bit-shift-right)
       :stdlib? true
       :doc ""
       :group :stdlib)

      ;; Forth-based

      (set-global-word-defn
       '. dot
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       '.s dot-stack
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       '<> swap
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       '>r push-return
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'cr carriage-return
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'drop op-drop
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'dup dup
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'nip nip
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'over over
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'r> pop-return
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'roll roll
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'rot rot
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'swap swap
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'tuck tuck
       :stdlib? true
       :doc ""
       :group :stdlib)

      ;;
      ;; Comparison Operators
      ;;

      (set-global-word-defn
       '< op-<
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       '<= op-<=
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       '= op-=
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       '> op->
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       '>= op->=
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'and (wrap-function-with-arity 2 #(and %1 %2))
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'compare (wrap-function-with-arity 2 compare)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'not (wrap-function-with-arity 1 not)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'not= op-not=
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'or (wrap-function-with-arity 2 #(or %1 %2))
       :stdlib? true
       :doc ""
       :group :stdlib)

      ;; CLJ Type Checking
      #?(:clj
         (set-global-word-defn
          'bytes? (wrap-function-with-arity 1 bytes?)
          :stdlib? true
          :doc ""
          :group :stdlib))

      #?(:clj
         (set-global-word-defn
          'ratio? (wrap-function-with-arity 1 ratio?)
          :stdlib? true
          :doc ""
          :group :stdlib))

      #?(:clj
         (set-global-word-defn
          'rational? (wrap-function-with-arity 1 rational?)
          :stdlib? true
          :doc ""
          :group :stdlib))

      #?(:clj
         (set-global-word-defn
          'decimal? (wrap-function-with-arity 1 decimal?)
          :stdlib? true
          :doc ""
          :group :stdlib))

      ;; Type Checking

      (set-global-word-defn
       'any? (wrap-function-with-arity 1 any?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'associative? (wrap-function-with-arity 1 associative?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'boolean? (wrap-function-with-arity 1 boolean?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'coll? (wrap-function-with-arity 1 coll?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'double? (wrap-function-with-arity 1 double?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'even? (wrap-function-with-arity 1 even?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'false? (wrap-function-with-arity 1 false?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'float? (wrap-function-with-arity 1 float?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'indexed? (wrap-function-with-arity 1 indexed?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'inst? (wrap-function-with-arity 1 inst?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'int? (wrap-function-with-arity 1 int?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'integer? (wrap-function-with-arity 1 integer?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'keyword? (wrap-function-with-arity 1 keyword?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'list? (wrap-function-with-arity 1 list?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'map? (wrap-function-with-arity 1 map?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'nat-int? (wrap-function-with-arity 1 nat-int?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'neg-int? (wrap-function-with-arity 1 neg-int?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'nil? (wrap-function-with-arity 1 nil?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'number? (wrap-function-with-arity 1 number?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'odd? (wrap-function-with-arity 1 odd?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'pos-int? (wrap-function-with-arity 1 pos-int?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'seq? (wrap-function-with-arity 1 seq?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'seqable? (wrap-function-with-arity 1 seqable?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'sequential? (wrap-function-with-arity 1 sequential?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'set? (wrap-function-with-arity 1 set?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'some? (wrap-function-with-arity 1 some?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'string? (wrap-function-with-arity 1 string?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'symbol? (wrap-function-with-arity 1 symbol?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'true? (wrap-function-with-arity 1 true?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'uri? (wrap-function-with-arity 1 uri?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'uuid? (wrap-function-with-arity 1 uuid?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'vector? (wrap-function-with-arity 1 vector?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'zero? (wrap-function-with-arity 1 zero?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      ;; Other Stuff

      (set-global-word-defn
       'count (wrap-function-with-arity 1 count)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'deref (wrap-function-with-arity 1 deref)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'newline (wrap-procedure-with-arity 0 newline)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'pr (wrap-procedure-with-arity 1 pr)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'print (wrap-procedure-with-arity 1 print)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'println (wrap-procedure-with-arity 1 println)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'prn (wrap-procedure-with-arity 1 prn)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'rand (wrap-function-with-arity 1 rand)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'randn (wrap-function-with-arity 2 rand)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'str (wrap-function-with-arity 2 str)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'str/blank? (wrap-function-with-arity 1 str/blank?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'str/capitalize (wrap-function-with-arity 1 str/capitalize)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'str/ends-with? (wrap-function-with-arity 2 str/ends-with?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'str/escape (wrap-function-with-arity 2 str/escape)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'str/includes? (wrap-function-with-arity 2 str/includes?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'str/index-of (wrap-function-with-arity 2 str/index-of)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'str/join (wrap-function-with-arity 2 str/join)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'str/last-index-of (wrap-function-with-arity 2 str/last-index-of)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'str/lower-case (wrap-function-with-arity 1 str/lower-case)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'str/replace (wrap-function-with-arity 3 str/replace)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'str/replace-first (wrap-function-with-arity 3 str/replace-first)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'str/split (wrap-function-with-arity 2 str/split)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'str/split-lines (wrap-function-with-arity 1 str/split-lines)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'str/starts-with? (wrap-function-with-arity 2 str/starts-with?)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'str/trim (wrap-function-with-arity 1 str/trim)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'str/trim-newline (wrap-function-with-arity 1 str/trim-newline)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'str/triml (wrap-function-with-arity 1 str/triml)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'str/trimr (wrap-function-with-arity 1 str/trimr)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'str/upper-case (wrap-function-with-arity 1 str/upper-case)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'subs (wrap-function-with-arity 3 subs)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'subvec (wrap-function-with-arity 3 subvec)
       :stdlib? true
       :doc ""
       :group :stdlib)

      ;; Regex
      (set-global-word-defn
       're-find (wrap-function-with-arity 2 re-find)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       're-find-match (wrap-function-with-arity 1 re-find)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       're-seq (wrap-function-with-arity 2 re-seq)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       're-matches (wrap-function-with-arity 2 re-matches)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       're-pattern (wrap-function-with-arity 1 re-pattern)
       :stdlib? true
       :doc ""
       :group :stdlib)

      (set-global-word-defn
       'regex (wrap-function-with-arity 1 re-pattern)
       :stdlib? true
       :doc ""
       :group :stdlib)

      ;; CLJ Specific
      #?(:clj
         (set-global-word-defn
          'class (wrap-function-with-arity 1 class)
          :stdlib? true
          :doc ""
          :group :stdlib))

      #?(:clj
         (set-global-word-defn
          're-matcher (wrap-function-with-arity 2 re-matcher)
          :stdlib? true
          :doc ""
          :group :stdlib))

      #?(:clj
         (set-global-word-defn
          're-groups (wrap-function-with-arity 1 re-groups)
          :stdlib? true
          :doc ""
          :group :stdlib))

      ))

