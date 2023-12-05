(ns day10
  (:require [clojure.string :as string]
            [clojure.set :as set]
            [clojure.java.io :as io]))

(defn gcf
  [a b]
  (long (.gcd (biginteger a)
              (biginteger b))))

(defn slope
  "returns simplified slope"
  [[x1 y1] [x2 y2]]
  (let [slope [(- x2 x1) (- y2 y1)]
        factor (apply gcf slope)]
    (if (zero? factor)
      slope
      (map #(/ % factor)
           slope))))

(defn num-stations
  [coords root]
  (->> coords
       (map (partial slope root))
       (distinct)
       (remove #{[0 0]})
       (count)))

(defn asteroid-coordinates
  [m]
  (for [x (-> m (count) (range))
        y (-> m (first) (count) (range))
        :when (= \# (get-in m [x y]))]
    [y x]))

(defn max-station
  [m]
  (let [coords (asteroid-coordinates m)]
    (->> coords
         (map (juxt identity (partial num-stations coords)))
         (reduce (fn [[coord1 num1] [coord2 num2]]
                   (if (>= num2 num1)
                     [coord2 num2]
                     [coord1 num1]))))))

(defn parse
  [inputstr]
  (->> (string/split inputstr #"\n")
       (remove empty?)
       (mapv vec)))

(defn part1
  [inputstr]
  (-> inputstr
      (parse)
      (max-station)))

#_
(part1 (slurp (io/resource "day10_input.txt"))) ; => [[23 20] 334]

;;;;;;;;;;;;
;; PART 2
;; find 200th asteroid to be vaporized
;; find all slopes and distances, sort by slope, distance, take 200th element.
;;;;;;;;;;;;

(def sq (comp (partial apply *) (partial repeat 2)))

(defn distance
  [[x1 y1] [x2 y2]]
  (Math/sqrt (+ (sq (- x2 x1))
                (sq (- y2 y1)))))

(defn degrees
  [[x y]]
  (* (Math/atan2 y x)
     (/ 180 Math/PI)))

(defn rotate
  [rotation deg]
  (mod (+ deg rotation)
       360))

(defn angle
  [root coord]
  (->> (slope root coord)
       (degrees)
       (rotate 90)))

(defn list-circles
  [m]
  (loop [m m
         acc []]
    (if (empty? m)
      acc
      (recur (->> m
                  (map #(update % 1 rest))
                  (remove (comp empty? second)))
             (->> m
                  (map (comp second first second))
                  (concat acc))))))

(defn angle-map
  [coll]
  (reduce (fn [acc [coord angle distance]]
            (if (acc angle)
              (update acc angle conj [distance coord])
              (assoc acc angle (sorted-set [distance coord]))))
          (sorted-map)
          coll))

(defn angles-distances
  [m root]
  (->> m
       (asteroid-coordinates)
       (map (juxt identity
                  (partial angle root)
                  (partial distance root)))
       (remove (comp (partial = root) first))
       (angle-map)
       (list-circles)))

(defn part2
  [inputstr root]
  (-> inputstr
      (parse)
      (angles-distances root)
      #_(nth 199)))

#_
(let [[x y] (nth (part2 (slurp (io/resource "day10_input.txt"))
                     [23 20])
                 199)]
  (+ (* 100 x) y))
;; => 1119
