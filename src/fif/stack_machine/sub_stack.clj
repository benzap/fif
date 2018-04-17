(ns fif.stack-machine.sub-stack)


(defn create-sub-stack [coll]
  (conj coll '()))


(defn push-sub-stack [coll x]
  (let [f (conj (peek coll) x)]
    (-> coll pop (conj f))))


(defn pop-sub-stack [coll]
  (let [f (-> coll peek pop)]
    (-> coll pop (conj f))))


(defn get-sub-stack [coll]
  (-> coll peek))


(defn set-sub-stack [coll x]
  (-> coll pop (conj x)))


(defn remove-sub-stack [coll]
  (pop coll))
