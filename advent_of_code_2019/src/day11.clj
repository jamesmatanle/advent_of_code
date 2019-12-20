(ns day11
  (:require [day09]
            [clojure.core.async :as async]
            [clojure.java.io :as io]
            [incanter.core :as incanter]
            [incanter.charts :as chart]))

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

(comment
  (next-direction 0 0)
  (next-direction 0 1)
  (next-direction 1 0)
  (next-direction 1 1)
  (next-direction 2 0)
  (next-direction 2 1)
  (next-direction 3 0)
  (next-direction 3 1))

(defn exec-loop
  [in out initial-color]
  (loop [color-map {[0 0] initial-color}
         direction 0
         position [0 0]]
    (async/>!! out (color-map position 0))
    (let [[val ch] (async/alts!! [in (async/timeout 1000)])]
      (if (identical? in ch)
        (if-let [new-direction (next-direction direction (async/<!! in))]
          (recur (assoc color-map position val)
                 new-direction
                 (next-position position new-direction))
          color-map)
        color-map))))

(defn f
  [memorystr initial-color]
  (let [brain-in (async/chan 1)
        brain-out (async/chan 2)]
    (future
      (day09/execute (day09/parse-memory-string memorystr)
                     brain-in
                     brain-out))
    (exec-loop brain-out brain-in initial-color)))

#_
(count (f (slurp (io/resource "day11_input.txt"))
          0))
;; => 2276
;; => 2276

;;;;;;;;;;;;
;; PART 2
;; change initial color to white, then plot coordinates to read message.
;;;;;;;;;;;;
;;;;;;;;;;;;
;; PART 2
;;;;;;;;;;;;

;; filter out all the white spots, plot them using xy-plot
#_
(let [xy (->> (f (slurp (io/resource "day11_input.txt"))
                 1)
              (filter (fn [[k v]] (= 1 v)))
              (map first))]
  (incanter/view
   (chart/scatter-plot (map first xy)
                       (map second xy))))
;; => CBLPJZCU
