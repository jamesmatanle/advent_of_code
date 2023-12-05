(ns day11
  "https://adventofcode.com/2019/day/11"
  (:require [day09]
            [clojure.core.async :as async]
            [clojure.java.io :as io]))

(defn next-position
  [[x y] direction]
  (assert (#{0 1 2 3} direction))
  (case direction
    0 [x (inc y)] ; up
    1 [(inc x) y] ; right
    2 [x (dec y)] ; down
    3 [(dec x) y])) ; left

(defn next-direction
  "-1: rotate left 90 degrees. 1: rotate right 90 degrees"
  [direction command]
  (assert (#{0 1 2 3} direction))
  (assert (contains? #{0 1 nil} command))
  (if command
    (mod (+ direction
            (case command 0 -1 1 1 nil 0))
         4)
    nil))

(defn exec-loop
  [in out initial-color]
  (loop [color-map {[0 0] initial-color}
         direction 0
         position [0 0]]
    (async/>!! out (constantly (color-map position 0)))
    (let [[val ch] (async/alts!! [in (async/timeout 1000)])]
      (if (identical? in ch)
        (if-let [new-direction (next-direction direction (async/<!! in))]
          (recur (assoc color-map position val)
                 new-direction
                 (next-position position new-direction))
          color-map)
        color-map))))

(defn robot
  [memorystr initial-color]
  (let [brain-in (async/chan 1)
        brain-out (async/chan 2)]
    (future
      (day09/execute-string memorystr brain-in brain-out))
    (exec-loop brain-out brain-in initial-color)))

(defn part1
  []
  (count
   (robot (util/fstr "day11_input.txt")
          0)))

#_
(part1)
;; => 2276

;;;;;;;;;;;;
;; PART 2
;; change initial color to white, then plot coordinates to read message.
;;;;;;;;;;;;

(defn plot-coordinates
  [coordinates]
  (let [xs (map (comp first first) coordinates)
        ys (map (comp second first) coordinates)]
    (for [y (range (apply max ys) (dec (apply min ys)) -1)]
      (for [x (range (apply min xs) (inc (apply max xs)))]
        (if (zero? (get coordinates [x y] 0))
          " "
          1)))))

(defn part2
  []
  (->> (robot (util/fstr "day11_input.txt") 1)
       (plot-coordinates)
       (mapv println)))

#_
(part2)
;; =>
;; (    1 1     1 1 1     1         1 1 1         1 1   1 1 1 1     1 1     1     1      )
;; (  1     1   1     1   1         1     1         1         1   1     1   1     1      )
;; (  1         1 1 1     1         1     1         1       1     1         1     1      )
;; (  1         1     1   1         1 1 1           1     1       1         1     1      )
;; (  1     1   1     1   1         1         1     1   1         1     1   1     1      )
;; (    1 1     1 1 1     1 1 1 1   1           1 1     1 1 1 1     1 1       1 1        )
;; => CBLPJZCU
