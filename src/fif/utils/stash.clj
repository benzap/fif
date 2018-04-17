(ns fif.utils.stash
  "Functions for manipulating stashes, not to be confused with stacks.
  A stash in this case is collection of containers. The containers are
  sequenced fifo-style, with operations performed on the latest
  container pushed into the collection of stash containers.")


(defonce *gen-increment (atom 0))
(defn get-unique-id [] (swap! *gen-increment inc))
;; (get-unique-id)


(defn create-stash [] [])


(defn new-stash
  ([stash container]
   (conj stash container))
  ([stash] (new-stash stash {})))


(defn update-stash
  [stash f & args]
  (let [index (-> stash count dec)]
    (apply update-in stash [index] f args)))


(defn remove-stash [stash] (pop stash))


(defn peek-stash [stash] (peek stash))
