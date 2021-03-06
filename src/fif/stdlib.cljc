(ns fif.stdlib
  "Includes all of the standard library functions and modes for fif."
  (:require
   [fif.stdlib.ops :refer [import-stdlib-ops]]
   [fif.stdlib.cond-loop :refer [import-stdlib-cond-loop-mode]]
   [fif.stdlib.compile :refer [import-stdlib-compile-mode]]
   [fif.stdlib.conditional :refer [import-stdlib-conditional-mode]]
   [fif.stdlib.variable :refer [import-stdlib-variable-mode]]
   [fif.stdlib.constant :refer [import-stdlib-constant-mode]]
   [fif.stdlib.macro :refer [import-stdlib-macro-mode]]
   [fif.stdlib.math :refer [import-stdlib-math]]
   [fif.stdlib.collecter :refer [import-stdlib-collecter-mode]]
   [fif.stdlib.collection-ops :refer [import-stdlib-collection-ops]]
   [fif.stdlib.functional-ops :refer [import-stdlib-functional-ops]]
   [fif.stdlib.realizer :refer [import-stdlib-realize-mode]]
   [fif.stdlib.realizer.multi :refer [import-stdlib-realize-multi-mode]]
   [fif.stdlib.tools :refer [import-stdlib-stack-tools]]
   [fif.stdlib.repl :refer [import-stdlib-repl]]
   [fif.stdlib.reader :refer [import-stdlib-reader]]
   [fif.stdlib.type-checking :refer [import-stdlib-type-checking]]
   [fif.stdlib.piecewise :refer [import-stdlib-piecewise]]
   [fif.stdlib.help :refer [import-stdlib-help]]
   [fif.stdlib.destructuring :refer [import-stdlib-destructuring]]
   [fif.stdlib.s-expression :refer [import-stdlib-s-expression]]))


(defn import-stdlib [sm]
  (-> sm
      import-stdlib-ops
      import-stdlib-compile-mode
      import-stdlib-conditional-mode
      import-stdlib-cond-loop-mode
      import-stdlib-variable-mode
      import-stdlib-constant-mode
      import-stdlib-macro-mode
      import-stdlib-math
      import-stdlib-collecter-mode
      import-stdlib-collection-ops
      import-stdlib-functional-ops
      import-stdlib-realize-mode
      import-stdlib-realize-multi-mode
      import-stdlib-stack-tools
      import-stdlib-repl
      import-stdlib-reader
      import-stdlib-type-checking
      import-stdlib-piecewise
      import-stdlib-help
      import-stdlib-destructuring
      import-stdlib-s-expression))
