(ns day10
  (:require [clojure.string :as string]
            [clojure.set :as set]
            [clojure.java.io :as io]))

(defn gcf
  [a b]
  (long (.gcd (biginteger a)
              (biginteger b))))

(comment
  (gcf 3 2)
  (gcf 6 4)
  (gcf 9 6)
  (gcf 12 8)
  )

(defn slope
  "returns simplified slope"
  [[x1 y1] [x2 y2]]
  (let [slope [(- x2 x1) (- y2 y1)]
        factor (apply gcf slope)]
    (if (zero? factor)
      slope
      (map #(/ % factor)
           slope))))

(comment
  (slope [0 0] [12 8])
  (slope [0 0] [9 8])
  (slope [0 0] [0 2])
  (slope [0 0] [0 1])
  (slope [0 0] [1 0])
  (slope [0 0] [2 0])
  )

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

(defn f
  [inputstr]
  (-> inputstr
      (parse)
      (max-station)))

#_
(f
 "
.#..#
.....
#####
....#
...##") ; => [[3 4] 8]

#_
(f
 "
......#.#.
#..#.#....
..#######.
.#.#.###..
.#..#.....
..#....#.#
#..#....#.
.##.#..###
##...#..#.
.#....####") ; => [[5 8] 33]

#_
(f
 "
#.#...#.#.
.###....#.
.#....#...
##.#.#.#.#
....#.#.#.
.##..###.#
..#...##..
..##....##
......#...
.####.###.") ; => [[1 2] 35]

#_
(f
 "
.#..#..###
####.###.#
....###.#.
..###.##.#
##.##.#.#.
....###..#
..#.#..#.#
#..#.#.###
.##...##.#
.....#.#..") ; => [[6 3] 41]

(def large-input
  "
.#..##.###...#######
##.############..##.
.#.######.########.#
.###.#######.####.#.
#####.##.#.##.###.##
..#####..#.#########
####################
#.####....###.#.#.##
##.#################
#####.##.###..####..
..######..##.#######
####.##.####...##..#
.#####..#.######.###
##...#.##########...
#.##########.#######
.####.#.###.###.#.##
....##.##.###..#####
.#.#.###########.###
#.#.#.#####.####.###
###.##.####.##.#..##")

#_
(f large-input) ; => [[11 13] 210]

#_
(f (slurp (io/resource "day10_input.txt"))) ; => [[23 20] 334]

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

#_
(distance [0 0] [1 1])

(defn degrees
  [[x y]]
  (* (Math/atan2 y x)
     (/ 180 Math/PI)))

(comment
  (degrees [1 0])
  (degrees [0 1])
  (degrees [0 -1]) (degrees [-1 -1])
  (degrees [1 1])
  )

(defn rotate
  [rotation deg]
  (mod (+ deg rotation)
       360))

(defn angle
  [root coord]
  (->> (slope root coord)
       (degrees)
       (rotate 90)))

#_
(angle [0 0] [0 1])

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

#_
(list-circles
 {0 [[1 [0 1]] [2 [0 2]]]
  90 [[1 [1 0]]]})
;; => ([0 1] [1 0] [0 2])

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

(defn f2
  [inputstr root]
  (-> inputstr
      (parse)
      (angles-distances root)
      #_(nth 199)))

#_
(f2 "
.#....#####...#..
##...##.#####..##
##...#...#.#####.
..#.....#...###..
..#.#.....#....##" [8 3])

;; => ([8 1] [9 0] [9 1] [10 0] [9 2] [11 1] [12 1] [11 2] [15 1] [12 2] [13 2] [14 2] [15 2] [12 3] [16 4] [15 4] [10 4] [4 4] [2 4] [2 3] [0 2] [1 2] [0 1] [1 1] [5 2] [1 0] [5 1] [6 1] [6 0] [7 0] [8 0] [10 1] [14 0] [16 1] [13 3] [14 3])


;; The 1st asteroid to be vaporized is at 11,12.
;; The 2nd asteroid to be vaporized is at 12,1.
;; The 3rd asteroid to be vaporized is at 12,2.
;; The 10th asteroid to be vaporized is at 12,8.
;; The 20th asteroid to be vaporized is at 16,0.
;; The 50th asteroid to be vaporized is at 16,9.
;; The 100th asteroid to be vaporized is at 10,16.
;; The 199th asteroid to be vaporized is at 9,6.
;; The 200th asteroid to be vaporized is at 8,2.
;; The 201st asteroid to be vaporized is at 10,9.
;; The 299th and final asteroid to be vaporized is at 11,1.
#_
(let [res (f2 large-input [11 13])]
  [(nth res 0)
   (nth res 1)
   (nth res 2)
   (nth res 9)
   (nth res 19)
   (nth res 49)
   (nth res 99)
   (nth res 198)
   (nth res 199)
   (nth res 200)
   (nth res 298)
   ])
;; => [[11 12] [12 1] [12 2] [12 8] [16 0] [16 9] [10 16] [9 6] [8 2] [10 9] [11 1]]

#_
(let [[x y] (nth (f2 (slurp (io/resource "day10_input.txt"))
                     [23 20])
                 199)]
  (+ (* 100 x)
     y)) ; => 1119
