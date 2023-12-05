(ns day06
  (:require [clojure.string :as string]
            [clojure.java.io :as io]))

(defn parse-to-map
  "create map from child to parent to model tree of relationships."
  [s]
  (->> (string/split s #"\n")
       (map #(string/split % #"\)"))
       (map reverse)
       (map vec)
       (into {})))

(defn children
  [m parent]
  (->> m
       (filter #(= parent (second %)))
       (map first)))

(defn count-relationships
  ([m]
   (count-relationships m 1 "COM"))
  ([m depth parent]
   (let [children (children m parent)]
     (if (seq children)
       (+ (* depth
             (count children))
          (->> children
               (map (partial count-relationships m (inc depth)))
               (reduce +)))
       0))))

(defn part1
  [inputstr]
  (-> inputstr
      (parse-to-map)
      (count-relationships)))

;; find COM. then find COM's children. +1 for direct relationship, +1 for each child. for each child, find children. +2 for direct and indirect relationships * +1 for each child (level 2).

#_
(part1 (util/fstr "day06_input.txt"))

;;;;;;;;;;;;
;; PART 2
;;;;;;;;;;;;

(defn lineage
  [m node]
  (if (m node)
    (conj (lineage m (m node)) node)
    [node]))

(defn remove-common-ancestry
  [l1 l2]
  (if (= (first l1) (first l2))
    (remove-common-ancestry (rest l1) (rest l2))
    [l1 l2]))

(defn num-transfers-to-santa
  [m]
  (->> [(lineage m "YOU") (lineage m "SAN")]
       (map butlast)
       (apply remove-common-ancestry)
       (apply concat)
       (count)))

(defn part2
  [inputstr]
  (-> inputstr
      (parse-to-map)
      (num-transfers-to-santa)))

#_
(part2 (util/fstr "day06_input.txt"))
