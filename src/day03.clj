(ns day03
  (:require [clojure.string :as string]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

;; follow the wires, store each location. check if any location from wire 2 is in wire 1. constant lookup via sets. constant time to check each position for match, so O(length of wire 1 + length of wire 2). return minimum of all matches' sum of x and y.

;; coordinate stored as simple list, duplicates will exist. would be faster to build set rather than build list and convert to set. however list is useful in part 2.

(defn add-coordinates-from-path
  [acc path]
  (let [oldx (first (last acc))
        oldy (second (last acc))
        size (->> path (rest) (apply str) (edn/read-string))]
    (concat acc
            (case (first path)
              \R (map vector
                      (map inc (range oldx (+ oldx size)))
                      (repeat oldy))
              \L (reverse (map vector
                               (range (- oldx size) oldx)
                               (repeat oldy)))
              \U (map vector
                      (repeat oldx)
                      (map inc (range oldy (+ oldy size))))
              \D (reverse (map vector
                               (repeat oldx)
                               (range (- oldy size) oldy)))
              (throw (ex-info "bad input" {}))))))

(defn all-coordinates
  [paths]
  (reduce add-coordinates-from-path [[0 0]] paths))

(defn parse
  [input]
  (map #(string/split % #",")
       (clojure.string/split input #"\n")))

(defn matches
  [set1 set2]
  (reduce (fn [acc elem]
            (if (set1 elem)
              (conj acc elem)
              acc))
          []
          set2))

(defn manhattan-from-origin
  [[x y]]
  (+ (Math/abs x) (Math/abs y)))

(defn part1
  [input]
  (->> input
       (parse)
       (map all-coordinates)
       (map rest)
       (map set)
       (apply matches)
       (map manhattan-from-origin)
       (apply min)))

#_
(part1 (util/fstr "day03_input.txt"))
;; => 855

;;;;;;;;;;;;
;; PART 2

;; Rather than converting positions into a set, convert positions into map of position to number of steps at _first_ time reaching position. Then lookup intersections, and do new manhattan on each intersection, using the lookup values.

(defn convert
  "convert each list into a map of positions to shortest number of steps to reach position."
  [positions]
  (zipmap (reverse positions)
          (->> positions
              (count)
              (range)
              (map inc)
              (reverse))))

(defn intersection-delays
  "returns a list of the intersection delays for all matches on 2 wires."
  [m1 m2]
  (reduce (fn [acc kv]
            (if (m1 (first kv))
              (conj acc
                    (+ (m1 (first kv))
                       (m2 (first kv))))
              acc))
          []
          m2))

(defn part2
  [input]
  (->> input
       (parse)
       (map all-coordinates)
       (map rest)
       (map convert)
       (apply intersection-delays)
       (apply min)))

#_
(part2 (util/fstr "day03_input.txt"))
;; => 11238
