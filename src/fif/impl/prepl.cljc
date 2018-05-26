(ns fif.impl.prepl
  "Implementation of programmable repl. Useful for implementing fif
  outside of a standard input/output environment."
  (:require
   [fif.stack-machine :as stack]
   [fif.stack-machine.evaluators :as evaluators]
   #?(:clj [fif.utils.display :refer [PrintWriter-on]])))


(defn prepl-eval
  "Programmable Repl Evaluation for clojure(script). Function is useful
  for a full repl implementation, since the output can be processed
  via `output-fn`.
   

   Keyword Arguments:
  
   sm -- Stack-machine

   input-string -- String representation of fif form to be evaluated.

   output-fn -- Function of the form (fn [{:keys [tag value]}]).


   Output Function Key Arguments:

   tag -- either :error from *err* output, :out from *out* output.

   value -- string value of the presented tag.


   Return Value:
  
   Returns an updated stack-machine after the `input-string` has been evaluated.

   Notes:

   - Standard Out is flushed after evaluation, however, while
  evaluating, output-fn will get called preceding any newline
  delimited string within the print writer.
   "
  [sm input-string output-fn]
  (binding
   #?(:clj [*out* (PrintWriter-on #(output-fn {:tag :out :value %1}) nil)
            *err* (PrintWriter-on #(output-fn {:tag :error :value %1}) nil)]
      :cljs [*print-newline* true
             *print-fn* #(output-fn {:tag :out :value %1})])
   (let [evaled-sm (evaluators/eval-string sm input-string)]
     (flush)
     evaled-sm)))
