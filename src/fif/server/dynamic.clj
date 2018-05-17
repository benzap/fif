(ns fif.server.dynamic
  "Represents dynamic bindings used between the server and repl
  instances for individual thread management of input and output.")


(def ^:dynamic *fif-session*
  "Represents the thread's currently bound session instance. By default,
  nil means it is the main thread.

  Map of Opts:
  
  :server-name - Current server that the session resides on

  :server-session-key - Current stack-machine session, which resides
  in fif.server.session/*server-session
  "
  nil)


(def ^:dynamic *fif-in*
  "Stream Reader for obtaining input into the session's fif
  stack-machine."
  nil)


(def ^:dynamic *fif-out*
  "Stream Writer for placing standard output. By default, this is
  written to stdout defined by *out*"
  *out*)


(def ^:dynamic *fif-err*
  "Stream Writer for placing standard error output. By default, this is
  written to stderr defined by *err*"
  *err*)
