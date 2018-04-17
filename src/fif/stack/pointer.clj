(ns fif.stack.pointer
  (:require
   [fif.utils.token :as utils.token]
   [fif.stdlib.reserved :as reserved]))


(defn arg-is-pointer?
  "Returns true if the given argument resembles a pointer. A pointer is
  any symbol which begins with reserved/pointer-subtoken."
  [arg]
  (and (symbol? arg)
       (utils.token/symbol-starts-with? arg reserved/pointer-subtoken)))


(defn trim-pointer-once [arg]
  (utils.token/symbol-ltrim-once arg reserved/pointer-subtoken))
