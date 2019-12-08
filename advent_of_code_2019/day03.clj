(ns day03
  (:require [day03-input]
            [clojure.string :as string]
            [clojure.edn :as edn]))

(defn- p [x] (clojure.pprint/pprint x) x)

;; follow the wires and store each location. check if any location from wire 2 is in wire 1, use sets for coordinates for constant lookup. O(length of wire 1 + length of wire 2) b/c constant time to check each position for match. return minimum of all matches' sum of x and y.

;; coordinate stored as simple list. duplicates will exist. would be faster to build set rather than build list and convert to set. It turns out that keeping list is reusable in part 2 though.

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

#_
(matches #{[0 0] [0 1] [0 2]} #{[2 1] [1 1] [0 1] [0 2]})

(defn manhattan-from-origin
  [[x y]]
  (p x)
  (p y)
  (+ (Math/abs x) (Math/abs y)))

(defn f
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
(-> "R8,U5,L5,D3"
    (parse)
    (first)
    (all-coordinates))

#_
(f "R75,D30,R83,U83,L12,D49,R71,U7,L72
U62,R66,U55,R34,D71,R55,D58,R83") ; => 159

#_
(f "R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51
U98,R91,D20,R16,D67,R40,U7,R15,U6,R7") ; => 135

#_
(f day03-input/input) ; => 855


;;;;;;;;;;;;
;; PART 2
;;;;;;;;;;;;

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

#_
(convert [[0 1] [0 2] [0 1]])

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

#_
(intersection-delays {[0 1] 1 [0 2] 3}
                     {[0 1] 2 [1 1] 2 [0 2] 5})

(defn f2
  [input]
  (->> input
       (parse)
       (map all-coordinates)
       (map rest)
       (map convert)
       (apply intersection-delays)
       (apply min)))

#_
(f2 "R8,U5,L5,D3
U7,R6,D4,L4") ; => 30

#_
(f2 "R75,D30,R83,U83,L12,D49,R71,U7,L72
U62,R66,U55,R34,D71,R55,D58,R83") ; => 610 steps

#_
(f2 "R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51
U98,R91,D20,R16,D67,R40,U7,R15,U6,R7") ; => 410 steps

#_
(f2 day03-input/input) ; correct
