(ns fif.stdlib.shell
  "Provides fif with a dash '-' separated argument processing mode. This
  performs a reach-ahead to word definitions, and stores the results
  in a stash which can be accessed by a word function."
  (:require
   [fif.stack-machine :as stack]))


