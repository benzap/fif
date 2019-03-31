(ns fif.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests doo-all-tests]]

   ;;[fif-test.utils]

   ;; Test Cases ./
   [fif.client-test]
   [fif.core-test]
   [fif.def-test]
   [fif.stack-machine-test]

   ;; Test Cases ./impl
   [fif.impl.prepl-test]

   ;; Test Cases ./stack_machine
   [fif.stack-machine.error-handling-test]
   [fif.stack-machine.evaluators-test]
   [fif.stack-machine.flags-test]
   [fif.stack-machine.pointer-test]
   [fif.stack-machine.processor-test]
   [fif.stack-machine.scope-test]
   [fif.stack-machine.stash-test]
   [fif.stack-machine.sub-stack-test]
   [fif.stack-machine.verification-test]
   [fif.stack-machine.words-test]
   
   ;; Test Cases ./stdlib
   [fif.stdlib.collecter-test]
   [fif.stdlib.collection-ops-test]
   [fif.stdlib.compile-test]
   [fif.stdlib.conditional-test]
   [fif.stdlib.cond-loop-test]
   [fif.stdlib.constant-test]
   [fif.stdlib.functional-ops-test]
   [fif.stdlib.macro-test]
   [fif.stdlib.math-test]
   [fif.stdlib.ops-test]
   [fif.stdlib.tools-test]
   [fif.stdlib.realizer-test]
   [fif.stdlib.repl-test]
   [fif.stdlib.piecewise-test]
   [fif.stdlib.variable-test]
   [fif.stdlib.destructuring-test]

   ;; Test Cases ./utils
   [fif.utils.scope-test]
   [fif.utils.stash-test]
   [fif.utils.token-test]

   ;; Functional Tests
   [fif-functional.prepl-test]
   ))

#_(doo-all-tests)

(doo-tests
 ;; Other
 ;;'fif-test.utils

 ;; Test Cases ./
 'fif.client-test
 'fif.core-test
 'fif.def-test
 'fif.stack-machine-test

 ;; Test Cases ./impl
 'fif.impl.prepl-test

 ;; Test Cases ./stack_machine
 'fif.stack-machine.error-handling-test
 'fif.stack-machine.evaluators-test
 'fif.stack-machine.flags-test
 'fif.stack-machine.pointer-test
 'fif.stack-machine.processor-test
 'fif.stack-machine.scope-test
 'fif.stack-machine.stash-test
 'fif.stack-machine.sub-stack-test
 'fif.stack-machine.verification-test
 'fif.stack-machine.words-test


 ;; Test Cases ./stdlib
 'fif.stdlib.collecter-test
 'fif.stdlib.collection-ops-test
 'fif.stdlib.compile-test
 'fif.stdlib.conditional-test
 'fif.stdlib.cond-loop-test
 'fif.stdlib.constant-test
 'fif.stdlib.functional-ops-test
 'fif.stdlib.macro-test
 'fif.stdlib.math-test
 'fif.stdlib.ops-test
 'fif.stdlib.tools-test
 'fif.stdlib.realizer-test
 'fif.stdlib.repl-test
 'fif.stdlib.piecewise-test
 'fif.stdlib.variable-test
 'fif.stdlib.destructuring-test


 ;; Test Cases ./utils
 'fif.utils.scope-test
 'fif.utils.stash-test
 'fif.utils.token-test


 ;; Functional Tests
 'fif-functional.prepl-test)
