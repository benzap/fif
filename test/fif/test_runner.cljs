(ns fif.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests doo-all-tests]]

   ;; Test Cases
   [fif.stdlib.ops-test]))


;;(doo-all-tests)

(doo-tests
 'fif.stdlib.ops-test)
