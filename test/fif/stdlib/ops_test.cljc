(ns fif.stdlib.ops-test
  (:require
   [clojure.test :refer [deftest testing is are] :include-macros true]
   [fif.stdlib.ops]
   [fif.stack-machine :as stack]
   [fif.core :as fif]
   [fif-test.utils :refer [teval are-eq*] :include-macros true]))


;;
;; Arithmetic
;;


(deftest test-*-op
  (are-eq*
   (teval 2 2 *)

   => '(4)

   (teval 2 2 * 2 *)

   => '(8)))


(deftest test-+-op
  (are-eq*
   (teval 2 2 +)

   => '(4)

   (teval 2 2 + 2 +)

   => '(6)))


(deftest test---op
  (are-eq*
   (teval 2 2 -)

   => '(0)

   (teval 4 2 -)

   => '(2)

   (teval 2 4 -)

   => '(-2)))


(deftest test-divide-op
  (are-eq*
   (teval 2 2 /)

   => '(1)


   (teval 4 2 /)

   => '(2)))

;; FIXME: ratios are not recognized in js
;;#?(:clj (is (= '(1/2) (teval 1 2 /)))))


(deftest test-abs-op
  (are-eq*
   (teval 2 abs)

   => '(2)

   (teval -2 abs)

   => '(2)))


(deftest test-dec-op
  (are-eq*
   (teval 2 dec)

   => '(1)

   (teval -3 dec)

   => '(-4)))


(deftest test-inc-op
  (are-eq*
   (teval 2 inc)

   => '(3)

   (teval -3 inc)

   => '(-2)))



(deftest test-max-op
  (are-eq*
   (teval 2 4 max)

   => '(4)

   (teval 2 -4 max)

   => '(2)))


(deftest test-mod-op
  (are-eq*
   (teval 2 1 mod)

   => '(0)

   (teval 1 2 mod)

   => '(1)))


(deftest test-negate-op
  (are-eq*
   (teval 1 negate)

   => '(-1)

   (teval -1 negate)

   => '(1)))


(deftest test-quot-op
  (are-eq*
   (teval 1 2 quot)

   => '(0)))


(deftest test-rem-op
  (are-eq*
   (teval 1 2 rem)

   => '(1)))


;;
;; Bitwise Tests
;;


(deftest test-bit-and-op
  (are-eq*
   (teval 2r1100 2r1001 bit-and)

   => '(8)

   (teval 12 9 bit-and)

   => '(8)

   (teval 0x08 0xFF bit-and)

   => '(8)))


(deftest test-bit-and-not-op
  (are-eq*
   (teval 2r1100 2r1001 bit-and-not)

   => '(4)))


(deftest test-bit-clear-op
  (are-eq*
   (teval 2r1011 3 bit-clear)

   => '(3)))


(deftest test-bit-flip-op
  (are-eq*
   (teval 2r1011 2 bit-flip)

   => '(15)))


(deftest test-bit-not-op
  (are-eq*
   (teval 2r0111 bit-not)

   => '(-8)))


(deftest test-bit-or-op
  (are-eq*
   (teval 2r1100 2r1001 bit-or)

   => '(13)))


(deftest test-bit-shift-left-op
  (are-eq*
   (teval 1 10 bit-shift-left)

   => '(1024)))


(deftest test-bit-shift-right-op
  (are-eq*
   (teval 2r1101 0 bit-shift-right)

   => '(13)

   (teval 2r1101 1 bit-shift-right)

   => '(6)

   (teval 2r1101 2 bit-shift-right)

   => '(3)))


(deftest test-bit-test-op
  (are-eq*
   (teval 2r1001 0 bit-test)

   => '(true)

   (teval 2r1001 1 bit-test)

   => '(false)

   (teval 2r1001 3 bit-test)

   => '(true)))


(deftest test-bit-xor-op
  (are-eq*
   (teval 2r1100 2r1001 bit-xor)

   => '(5)))


(deftest test-byte-op)
(deftest test-unsigned-bit-shift-right-op)



(deftest test-dot-op
  (testing "Testing '.' operator"
    (are-eq*
      (with-out-str (teval "Hello World" .))

      => "Hello World")))


(deftest test-dot-stack-op
  (testing "Testing '.s' operator"
    (are-eq*
      (with-out-str (teval .s))

      => (with-out-str (println "<0> ()")))))


(deftest test-<>-op
  (testing "Testing '<>' operator"
    (are-eq*
      (teval 1 2 <>)

      => '(2 1))))


(deftest test->r-op
  (testing "Testing '>r' operator"
    (are-eq*
      (teval 1 >r 2 >r 3 >r r> r> r>)

      => '(3 2 1))))


(deftest test-cr-op
  (testing "Testing 'cr' operator"
    (are-eq*
      (with-out-str (teval "Hello World" . cr))

      => "Hello World\n")))


(deftest test-drop-op
  (testing "Testing 'drop' operator"
    (are-eq*
      (teval 1 2 drop)

      => '(1))))


(deftest test-dup-op
  (testing "Testing 'dup' operator"
    (are-eq*
      (teval 1 dup dup dup)

      => '(1 1 1 1))))


(deftest test-nip-op
  (testing "Testing 'nip' operator"
    (are-eq*
      (teval 1 2 nip)

      => '(2))))


(deftest test-over-op
  (testing "Testing 'over' operator"
    (are-eq*
      (teval 1 2 over)

      => '(1 2 1))))


(deftest test-r>-op
  (testing "Testing 'r>' operator"
    (are-eq*
     (teval 1 >r 2 >r 3 >r r> r> r>)

     => '(3 2 1))))


#_(deftest test-roll-op
    (testing "Testing 'roll' operator"
      (are-eq*
       (teval roll)

       => '())))


(deftest test-rot-op
  (testing "Testing 'rot' operator"
    (are-eq*
     (teval 1 2 3 rot)

     => '(2 3 1))))


(deftest test-swap-op
  (testing "Testing 'swap' operator"
    (are-eq*
      (teval 1 2 3 swap)

      => '(1 3 2))))


(deftest test-tuck-op
  (testing "Testing 'tuck' operator"
    (are-eq*
      (teval 1 2 3 4 tuck)

      => '(1 2 4 3 4))))


;;
;; Comparison Operators
;;


(deftest test-<-op
  (testing "Testing '<' operator"
    (are-eq*
      (teval 0 1 <)

      => '(true))))


(deftest test-<=-op
  (testing "Testing '<=' operator"
    (are-eq*
      (teval 1 1 <=)

      => '(true))))


(deftest test-=-op
  (testing "Testing '=' operator"
    (are-eq*
      (teval 1 1 =)

      => '(true))))


(deftest test->-op
  (testing "Testing '>' operator"
    (are-eq*
      (teval 0 1 >)

      => '(false))))


(deftest test->=-op
  (testing "Testing '>=' operator"
    (are-eq*
      (teval 0 1 >=)

      => '(false))))


(deftest test-and-op
  (testing "Testing 'and' operator"
    (are-eq*
      (teval true false and)

      => '(false))))


(deftest test-compare-op
  (testing "Testing 'compare ' operator"
    (are-eq*
      (teval 1 0 compare)

      => '(1))))


(deftest test-not-op
  (testing "Testing 'not' operator"
    (are-eq*
      (teval true not)

      => '(false))))


(deftest test-not=-op
  (testing "Testing 'not=' operator"
    (are-eq*
      (teval 1 1 not=)

      => '(false))))


(deftest test-or-op
  (testing "Testing 'or' operator"
    (are-eq*
      (teval true false or)

      => '(true))))


;;
;; CLJ Type Checking
;;

(comment
  #?(:clj
     (deftest test-bytes?-op
       (testing "Testing 'bytes?)' operator"
         (are-eq*
          (teval 16 byte bytes?)

          => '())))))
  

(comment
 #?(:clj
    ratio?)
 #?(:clj
    rational?)
 #?(:clj
    decimal?))

;; Type Checking


(deftest test-any?-op
  (testing "Testing 'any?' operator"
    (are-eq*
      (teval nil any?)

      => '(true))))


(deftest test-associative?-op
  (testing "Testing 'associative?' operator"
    (are-eq*
      (teval {} associative?)

      => '(true))))


(deftest test-boolean?-op
  (testing "Testing 'boolean?' operator"
    (are-eq*
      (teval true boolean?)

      => '(true))))


(deftest test-coll?-op
  (testing "Testing 'coll?' operator"
    (are-eq*
      (teval [] coll?)

      => '(true))))


(deftest test-double?-op
  (testing "Testing 'double?' operator"
    (are-eq*
      (teval 1.1 double?)

      => '(true))))


(deftest test-even?-op
  (testing "Testing 'even?' operator"
    (are-eq*
      (teval 2 even?)

      => '(true))))


(deftest test-false?-op
  (testing "Testing 'false?' operator"
    (are-eq*
      (teval false false?)

      => '(true))))


(deftest test-float?-op
  (testing "Testing 'float?' operator"
    (are-eq*
      (teval 1.1 float?)

      => '(true))))


(deftest test-indexed?-op
  (testing "Testing 'indexed?' operator"
    (are-eq*
      (teval {} indexed?)

      => '(false)

      (teval [] indexed?)

      => '(true))))


(deftest test-inst?-op
  (testing "Testing 'inst?' operator"
    (are-eq*
      (teval #inst "1985-04-12T23:20:50.52Z" inst?)

      => '(true))))


(deftest test-int?-op
  (testing "Testing 'int?' operator"
    (are-eq*
      (teval 2 int?)

      => '(true))))


(deftest test-integer?-op
  (testing "Testing 'integer?' operator"
    (are-eq*
      (teval 2 integer?)

      => '(true))))


(deftest test-keyword?-op
  (testing "Testing 'keyword?' operator"
    (are-eq*
      (teval :test keyword?)

      => '(true))))


(deftest test-list?-op
  (testing "Testing 'list?' operator"
    (are-eq*
      (teval () list?)

      => '(true))))


(deftest test-map?-op
  (testing "Testing 'map?' operator"
    (are-eq*
      (teval {} map?)

      => '(true))))


(deftest test-nat-int?-op
  (testing "Testing 'nat-int?' operator"
    (are-eq*
      (teval -2 nat-int?)

      => '(false))))


(deftest test-neg-int?-op
  (testing "Testing 'neg-int?' operator"
    (are-eq*
      (teval -2 neg-int?)

      => '(true))))


(deftest test-nil?-op
  (testing "Testing 'nil?' operator"
    (are-eq*
      (teval nil nil?)

      => '(true))))


(deftest test-number?-op
  (testing "Testing 'number?' operator"
    (are-eq*
      (teval 42 number?)

      => '(true))))


(deftest test-odd?-op
  (testing "Testing 'odd?' operator"
    (are-eq*
      (teval 3 odd?)

      => '(true))))


(deftest test-pos-int?-op
  (testing "Testing 'pos-int?' operator"
    (are-eq*
      (teval -2 pos-int?)

      => '(false))))


(deftest test-seq?-op
  (testing "Testing 'seq?' operator"
    (are-eq*
      (teval [] seq?)

      => '(false))))


(deftest test-seqable?-op
  (testing "Testing 'seqable?' operator"
    (are-eq*
      (teval [] seqable?)

      => '(true))))


(deftest test-sequential?-op
  (testing "Testing 'sequential?' operator"
    (are-eq*
      (teval () sequential?)

      => '(true))))


(deftest test-set?-op
  (testing "Testing 'set?' operator"
    (are-eq*
      (teval #{} set?)

      => '(true))))


(deftest test-some?-op
  (testing "Testing 'some?' operator"
    (are-eq*
      (teval nil some?)

      => '(false))))


(deftest test-string?-op
  (testing "Testing 'string?' operator"
    (are-eq*
      (teval "test" string?)

      => '(true))))


(deftest test-symbol?-op
  (testing "Testing 'symbol?' operator"
    (are-eq*
      (teval _test__ symbol?)

      => '(true))))


(deftest test-true?-op
  (testing "Testing 'true?' operator"
    (are-eq*
      (teval true true?)

      => '(true))))


(deftest test-uri?-op
  (testing "Testing 'uri?' operator"
    (are-eq*
      (teval "http://clojuredocs.org/" uri?)

      => '(false))))


(deftest test-uuid?-op
  (testing "Testing 'uuid?' operator"
    (are-eq*
      (teval #uuid "f81d4fae-7dec-11d0-a765-00a0c91e6bf6" uuid?)

      => '(true))))


(deftest test-vector?-op
  (testing "Testing 'vector?' operator"
    (are-eq*
      (teval [] vector?)

      => '(true))))


(deftest test-zero?-op
  (testing "Testing 'zero?' operator"
    (are-eq*
      (teval 0 zero?)

      => '(true))))


;; Other Stuff


(deftest test-count-op
  (testing "Testing 'count' operator"
    (are-eq*
     (teval (1) count)

     => '(1))))


#_(deftest test-deref-op
    (testing "Testing 'deref' operator"
      (are-eq*
       (teval deref)

       => '())))


(deftest test-newline-op
  (testing "Testing 'newline' operator"
    (are-eq*
     (with-out-str (teval newline))

     => (with-out-str (newline)))))


(deftest test-pr-op
  (testing "Testing 'pr' operator"
    (are-eq*
      (with-out-str (teval "test" pr))

      => "\"test\"")))


(deftest test-print-op
  (testing "Testing 'print' operator"
    (are-eq*
      (with-out-str (teval "Hello World" print))

      => "Hello World")))


(deftest test-println-op
  (testing "Testing 'println' operator"
    (are-eq*
      (with-out-str (teval "Hello World" println))

      => (with-out-str (println "Hello World")))))


(deftest test-prn-op
  (testing "Testing 'prn' operator"
    (are-eq*
     (with-out-str (teval "Hello World" prn))

     => (with-out-str (prn "Hello World")))))


#_(deftest test-rand-op
    (testing "Testing 'rand' operator"
      (are-eq*
       (teval rand)

       => '())))


#_(deftest test-randn-op
    (testing "Testing 'randn' operator"
      (are-eq*
       (teval randn)

       => '())))


(deftest test-str-op
  (testing "Testing 'str' operator"
    (are-eq*
      (teval "Hello " "World" str)

      => '("Hello World"))))


(deftest test-str-blank?-op
  (testing "Testing 'str/blank?' operator"
    (are-eq*
      (teval " " str/blank?)

      => '(true))))


(deftest test-str-capitalize-op
  (testing "Testing 'str/capitalize' operator"
    (are-eq*
      (teval "hey there" str/capitalize)

      => '("Hey there"))))


(deftest test-str-ends-with?-op
  (testing "Testing 'str/ends-with?' operator"
    (are-eq*
      (teval "test" "st" str/ends-with?)

      => '(true))))


(deftest test-str-escape-op
  (testing "Testing 'str/escape' operator"
    (are-eq*
      (teval "Hello Clojure World!" {\! "!!!"} str/escape)

      => '("Hello Clojure World!!!"))))


(deftest test-str-includes?-op
  (testing "Testing 'str/includes?' operator"
    (are-eq*
      (teval "test" "es" str/includes?)

      => '(true))))


(deftest test-str-index-of-op
  (testing "Testing 'str/index-of' operator"
    (are-eq*
      (teval "test" "es" str/index-of)

      => '(1))))


(deftest test-str-join-op
  (testing "Testing 'str/join' operator"
    (are-eq*
      (teval ", " [1 2 3] str/join)

      => '("1, 2, 3"))))


(deftest test-str-last-index-of-op
  (testing "Testing 'str/last-index-of' operator"
    (are-eq*
      (teval "test" "es" str/last-index-of)

      => '(1))))


(deftest test-str-lower-case-op
  (testing "Testing 'str/lower-case' operator"
    (are-eq*
      (teval "WHAT" str/lower-case)

      => '("what"))))


(deftest test-str-replace-op
  (testing "Testing 'str/replace' operator"
    (are-eq*
      (teval "The color is red" "red" regex "blue" str/replace)

      => '("The color is blue"))))


(deftest test-str-replace-first-op
  (testing "Testing 'str/replace-first' operator"
    (are-eq*
      (teval "The color is red" "red" regex "blue" str/replace-first)

      => '("The color is blue"))))


(deftest test-str-split-op
  (testing "Testing 'str/split' operator"
    (are-eq*
      (teval "test" "Clojure is awesome!" " " regex str/split)

      => '("test" ["Clojure" "is" "awesome!"]))))


(deftest test-str-split-lines-op
  (testing "Testing 'str/split-lines' operator"
    (are-eq*
      (teval "test \n string" str/split-lines)

      => '(["test " " string"]))))


(deftest test-str-starts-with?-op
  (testing "Testing 'str/starts-with?' operator"
    (are-eq*
      (teval "test" "te" str/starts-with?)

      => '(true))))


(deftest test-str-trim-op
  (testing "Testing 'str/trim' operator"
    (are-eq*
      (teval "   test  " str/trim)

      => '("test"))))


(deftest test-str-trim-newline-op
  (testing "Testing 'str/trim-newline' operator"
    (are-eq*
      (teval "  test\n" str/trim-newline)

      => '("  test"))))


(deftest test-str-triml-op
  (testing "Testing 'str/triml' operator"
    (are-eq*
      (teval "  test  " str/triml)

      => '("test  "))))


(deftest test-str-trimr-op
  (testing "Testing 'str/trimr' operator"
    (are-eq*
      (teval "  test  " str/trimr)

      => '("  test"))))


(deftest test-str-upper-case-op
  (testing "Testing 'str/upper-case' operator"
    (are-eq*
      (teval "hello" str/upper-case)

      => '("HELLO"))))


(deftest test-subs-op
  (testing "Testing 'subs' operator"
    (are-eq*
      (teval "test" 1 3 subs)

      => '("es"))))


(deftest test-subvec-op
  (testing "Testing 'subvec' operator"
    (are-eq*
      (teval [1 2 3 4 5 6 7] 2 over count subvec)

      => '([3 4 5 6 7]))))


;;
;; Regex
;;


(deftest test-re-find-op
  (testing "Testing 're-find' operator"
    (are-eq*
      (teval "\\d+" regex "abc12345def" re-find)

      => '("12345"))))


(deftest test-re-seq-op
  (testing "Testing 're-seq' operator"
    (are-eq*
      (teval "\\d" regex "clojure 1.1.0" re-seq)

      => '(("1" "1" "0")))))


(deftest test-re-matches-op
  (testing "Testing 're-matches' operator"
    (are-eq*
     (teval "hello.*" regex "hello, world" re-matches)

     => '("hello, world"))))


#_(deftest test-re-pattern-op
    (testing "Testing 're-pattern' operator"
      (are-eq*
       (teval re-pattern)

       => '())))


#_(deftest test-regex-op
    (testing "Testing 'regex' operator"
      (are-eq*
       (teval regex)

       => '())))


      ;; CLJ Specific
(comment
 #?(:clj
    class)
 #?(:clj
    re-matcher)
 #?(:clj
    re-groups))
