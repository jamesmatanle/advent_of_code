(ns day06
  (:require [day06-input]
            [clojure.string :as string]))

(defn- p [x] (clojure.pprint/pprint x))

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

(defn f
  [inputstr]
  (-> inputstr
      (parse-to-map)
      (count-relationships)))

;; find COM. then find COM's children. +1 for direct relationship, +1 for each child. for each child, find children. +2 for direct and indirect relationships * +1 for each child (level 2).

(def small-input "COM)B
B)C
C)D
D)E
E)F
B)G
G)H
D)I
E)J
J)K
K)L")

#_
(parse small-input)

#_
(f small-input)

#_
(f day06-input/input)


;;;;;;;;;;;;
;; PART 2
;;;;;;;;;;;;

(def small-input-2 "COM)B
B)C
C)D
D)E
E)F
B)G
G)H
D)I
E)J
J)K
K)L
K)YOU
I)SAN")

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

(defn f2
  [inputstr]
  (-> inputstr
      (parse-to-map)
      (num-transfers-to-santa)))

#_
(f2 small-input-2)

#_
(f2 day06-input/input)
