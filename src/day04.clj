(ns day04
  (:require [clojure.edn :as edn]))

;; 6 digit number
;; within input range
;; 2 adjacent digits are the same
;; the digits never decrease (L->R)

(def input
  [245318 765747])

(defn has-2-same-adj?
  "returns first 2 adjacent digits that are the same, else empty."
  [coll]
  (true?
   (reduce (fn [acc elem]
             (if (= acc elem)
               (reduced true)
               elem))
           (first coll)
           (rest coll))))

(defn digits-never-decrease?
  ([digits]
   (digits-never-decrease? (first digits) (rest digits)))
  ([x digits]
   (cond
     (empty? digits) true
     (<= x (first digits)) (digits-never-decrease? (first digits)
                                                   (rest digits))
     :else false)))

(defn parse-to-sequence
  [number]
  (->> number
       (String/valueOf)
       (map (comp edn/read-string str))))

(defn good-number?
  [elem]
  ((every-pred digits-never-decrease?
               has-2-same-adj?)
   (parse-to-sequence elem)))

(defn part1
  [input]
  (->> (apply range input)
       (reduce (fn [acc elem]
                 (if (good-number? elem)
                   (+ acc 1)
                   acc))
               0)))

#_
(part1 input)
;; => 1079

(defn adjacent-groups
  [coll]
  (reduce (fn [acc elem]
            (if (= (first (last acc)) elem)
              (conj (vec (butlast acc))
                    (conj (last acc)
                          elem))
              (conj acc [elem])))
          []
          coll))

(defn has-2-same-adj?-2
  "returns first 2 adjacent digits that are the same, else empty."
  [coll]
  (->> coll
       (adjacent-groups)
       (filter (fn [x]
                 (= 2 (count x))))
       (seq)
       (boolean)))

#_
(with-redefs [has-2-same-adj? has-2-same-adj?-2]
  (part1 input))
;; => 699
