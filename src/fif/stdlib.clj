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
   [fif.stdlib.collecter :refer [import-stdlib-collecter-mode]]
   [fif.stdlib.collection-ops :refer [import-stdlib-collection-ops]]))



(defn import-stdlib [sm]
  (-> sm
      (import-stdlib-ops)
      (import-stdlib-compile-mode)
      (import-stdlib-conditional-mode)
      (import-stdlib-cond-loop-mode)
      (import-stdlib-variable-mode)
      (import-stdlib-constant-mode)
      (import-stdlib-macro-mode)
      (import-stdlib-collecter-mode)
      (import-stdlib-collection-ops)))

