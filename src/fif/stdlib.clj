(ns fif.stdlib
  "Includes all of the primitive standard library functions for
  fif. These functions are stored as a mergeable map to be used by any
  stack machine.

  Notes:

  - Most of the functions listed were taken from the Forth standard library."
  (:refer-clojure :exclude [eval])
  (:require [fif.stack :as stack :refer :all]
            [fif.def :refer []]
            [fif.stdlib.ops :refer [import-stdlib-ops]]
            [fif.stdlib.compile :refer [import-stdlib-compile-mode]]
            [fif.stdlib.conditional :refer [import-stdlib-conditional-mode]]
            [fif.stdlib.variable :refer [import-stdlib-variable-mode]]
            [fif.stdlib.constant :refer [import-stdlib-constant-mode]]))



(defn import-stdlib [sm]
  (-> sm
      (import-stdlib-ops)
      (import-stdlib-compile-mode)
      (import-stdlib-conditional-mode)
      (import-stdlib-variable-mode)
      (import-stdlib-constant-mode)))
