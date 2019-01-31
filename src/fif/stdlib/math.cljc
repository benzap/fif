(ns fif.stdlib.math
  (:require
   [fif.stack-machine :as stack]
   [fif.stack-machine.words :refer [set-global-word-defn
                                    set-global-meta]]
   [fif.stack-machine.variable :refer [wrap-global-variable]]
   [fif.def :as def :refer [wrap-function-with-arity
                            wrap-procedure-with-arity]
    :include-macros true]))


(defn import-stdlib-math
  [sm]

  (-> sm
   
      (set-global-word-defn
       'PI (wrap-global-variable #?(:clj Math/PI :cljs (.-PI js/Math)))
       :stdlib? true
       :doc "The ratio of the circumference of a circle to its diameter"
       :group :stdlib.math)

      (set-global-word-defn
       'E (wrap-global-variable #?(:clj Math/E :cljs (.-E js/Math)))
       :stdlib? true
       :doc "The base of the natural logarithms"
       :group :stdlib.math)

      (set-global-word-defn
       'acos (wrap-function-with-arity 1 #?(:clj #(Math/acos %) :cljs (.-acos js/Math)))
       :stdlib? true
       :doc "( n -- n ) Returns the arccosine of x, in radians"
       :group :stdlib.math)

      (set-global-word-defn
       'asin (wrap-function-with-arity 1 #?(:clj #(Math/asin %) :cljs (.-asin js/Math)))
       :stdlib? true
       :doc "( n -- n ) Returns the arcsine of x, in radians"
       :group :stdlib.math)

      (set-global-word-defn
       'atan (wrap-function-with-arity 1 #?(:clj #(Math/atan %) :cljs (.-atan js/Math)))
       :stdlib? true
       :doc "( n -- n ) Returns the arctangent of x, in radians"
       :group :stdlib.math)

      (set-global-word-defn
       'atan2 (wrap-function-with-arity 2 #?(:clj #(Math/atan2 %1 %2) :cljs (.-atan2 js/Math)))
       :stdlib? true
       :doc "( x y -- n ) Returns the angle theta from the conversion of rectangular coordinates (x, y) to polar coordinates (r, theta)."
       :group :stdlib.math)

      (set-global-word-defn
       'cbrt (wrap-function-with-arity 1 #?(:clj #(Math/cbrt %) :cljs (.-cbrt js/Math)))
       :stdlib? true
       :doc "( n -- n ) Returns the cube root of a value."
       :group :stdlib.math)

      (set-global-word-defn
       'ceil (wrap-function-with-arity 1 #?(:clj #(Math/ceil %) :cljs (.-ceil js/Math)))
       :stdlib? true
       :doc "( n -- i ) Returns the smallest closest value that is greater than or equal to the given value as an integer."
       :group :stdlib.math)

      (set-global-word-defn
       'cos (wrap-function-with-arity 1 #?(:clj #(Math/cos %) :cljs (.-cos js/Math)))
       :stdlib? true
       :doc "( n -- n ) Returns the cosine of x, in radians"
       :group :stdlib.math)

      (set-global-word-defn
       'cosh (wrap-function-with-arity 1 #?(:clj #(Math/cosh %) :cljs (.-cosh js/Math)))
       :stdlib? true
       :doc "( n -- n ) Returns the hyperbolic cosine of x, in radians"
       :group :stdlib.math)

      (set-global-word-defn
       'exp (wrap-function-with-arity 1 #?(:clj #(Math/exp %) :cljs (.-exp js/Math)))
       :stdlib? true
       :doc "( n -- n ) Returns the value of E^n"
       :group :stdlib.math)

      (set-global-word-defn
       'floor (wrap-function-with-arity 1 #?(:clj #(Math/floor %) :cljs (.-floor js/Math)))
       :stdlib? true
       :doc "( n -- i ) Returns the largest closest value that is greater than or equal to the given value as an integer."
       :group :stdlib.math)

      (set-global-word-defn
       'log (wrap-function-with-arity 1 #?(:clj #(Math/log %) :cljs (.-log js/Math)))
       :stdlib? true
       :doc "( n -- n ) Returns the natural logarithm (base e) of value."
       :group :stdlib.math)

      (set-global-word-defn
       'pow (wrap-function-with-arity 2 #?(:clj #(Math/pow %1 %2) :cljs (.-pow js/Math)))
       :stdlib? true
       :doc "( x y -- n ) Returns the value of x to the power of y."
       :group :stdlib.math)

      (set-global-word-defn
       'round (wrap-function-with-arity 1 #?(:clj #(Math/round %) :cljs (.-round js/Math)))
       :stdlib? true
       :doc "( n -- n ) Returns the rounded value."
       :group :stdlib.math)

      (set-global-word-defn
       'sin (wrap-function-with-arity 1 #?(:clj #(Math/sin %) :cljs (.-sin js/Math)))
       :stdlib? true
       :doc "( n -- n ) Returns the sine of x, in radians"
       :group :stdlib.math)

      (set-global-word-defn
       'sinh (wrap-function-with-arity 1 #?(:clj #(Math/sinh %) :cljs (.-sinh js/Math)))
       :stdlib? true
       :doc "( n -- n ) Returns the hyperbolic sine of x, in radians"
       :group :stdlib.math)

      (set-global-word-defn
       'sqrt (wrap-function-with-arity 1 #?(:clj #(Math/sqrt %) :cljs (.-sqrt js/Math)))
       :stdlib? true
       :doc "( n -- n ) Returns the square root of the value."
       :group :stdlib.math)

      (set-global-word-defn
       'tan (wrap-function-with-arity 1 #?(:clj #(Math/tan %) :cljs (.-tan js/Math)))
       :stdlib? true
       :doc "( n -- n ) Returns the tangent of x, in radians"
       :group :stdlib.math)

      (set-global-word-defn
       'tanh (wrap-function-with-arity 1 #?(:clj #(Math/tanh %) :cljs (.-tanh js/Math)))
       :stdlib? true
       :doc "( n -- n ) Returns the hyperbolic tangent of x, in radians"
       :group :stdlib.math)))
