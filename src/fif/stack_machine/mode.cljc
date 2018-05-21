(ns fif.stack-machine.mode
  "Includes functions for creating mode functions within the stack
  machine."
  (:require
   [fif.stack-machine :as stack]
   [fif.stack-machine.stash :as stash]))


(def default-mode-stash
  {::flag nil
   ::state {}})


(defn new-mode-stash
  [sm flag state]
  (let [init-stash
        (assoc default-mode-stash
               ::flag flag
               ::state state)]
    (-> sm (stash/new-stash init-stash))))
  

(defn enter-mode
  ([sm flag state]
   (-> sm
       (stack/push-flag flag)
       (new-mode-stash flag state)))
  ([sm flag] (enter-mode sm flag {})))
  

(defn exit-mode
  [sm]
  (-> sm
      stack/pop-flag
      stash/remove-stash))


(defn set-state
  [sm state]
  (stash/update-stash assoc ::state state))


(defn update-state
  [sm f & args]
  (apply stash/update-stash sm update-in [::state] f args))


(def update-stash stash/update-stash)


(defn get-mode-stash
  [sm]
  (-> sm stash/peek-stash))


;; TODO: have it check if the stash goes missing, and assign to an
;; error state dispatch.
(defn mode-dispatch-fn
  "Function used with defmulti for stack mode dispatch based on state
  stored in the stash. This is used in tandom with `enter-mode` and
  `exit-mode`."
  [sm]
  (-> sm stash/peek-stash ::state))


(comment
  (defmulti functional-mode mode-dispatch-fn)
  (defmethod functional-mode {}
    [sm]
    (-> sm
        (set-state {:op ::reduce})))

  (defmethod functional-mode {:op ::reduce}
    [sm]
    (comment do-stuff)))
