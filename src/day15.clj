(ns day15
  "https://adventofcode.com/2019/day/15"
  (:require [util]
            [day09]
            [clojure.core.async :as async]))

;; inputs 1 = North. 2 = South. 3 = West. 4 = East
;; outputs
;; 0: The repair droid hit a wall. Its position has not changed.
;; 1: The repair droid has moved one step in the requested direction.
;; 2: The repair droid has moved one step in the requested direction; its new position is the location of the oxygen system.

;; NOTE Unlike typical search algorithm, the robot's exploration / position is in another computer, so must walk backwards to consider other directions.
;; Explore every square to know shortest path. Explore every path as long as it is unknown or shorter than known.

;; use depth first search (assume world is bound by walls)

(defn opposite
  [input]
  (case input
    1 2
    2 1
    3 4
    4 3))

(defn new-position
  [[y x] input]
  (case input
    1 [(inc y) x]
    2 [(dec y) x]
    3 [y (dec x)]
    4 [y (inc x)]))

(defn try-travel?
  "Return true iff new position is unknown or it is not a wall and cheaper to travel to on current path."
  [state candidate-position candidate-distance]
  (or (nil? (state candidate-position))
      (< candidate-distance
         (state candidate-position))))

(defn search-loop
  [input-ch output-ch state-atom]
  ((fn search
     [position]
     (doseq [input [1 2 3 4]]
       (let [new-position (new-position position input)
             new-distance (inc (@state-atom position))]
         (when (try-travel? @state-atom new-position new-distance)
           (async/>!! input-ch (constantly input))
           (let [output (async/<!! output-ch)]
             (if (= 0 output)
               (swap! state-atom assoc new-position Integer/MIN_VALUE) ; min integer never cheap enough to travel to.
               (do (when (= 2 output)
                     (swap! state-atom assoc :result new-position))
                   (swap! state-atom assoc new-position (inc (@state-atom position)))
                   (search new-position)
                   ;; Apply OPPOSITE input to return to old
                   ;; position and allow the loop to consider other directions.
                   ;; throw away output for this change in position.
                   (async/>!! input-ch (constantly (opposite input)))
                   (async/<!! output-ch))))))))
   [0 0]))

(defn part1
  []
  (let [input-ch (async/chan)
        output-ch (async/chan 100000)
        state-atom (atom {[0 0] 0})]
    ;; ROBOT ON INTCODE COMPUTER
    (future
      (try
        (day09/execute-string (util/fstr "day15_input.txt") input-ch output-ch)
        (catch Exception e
          (println e))))
    ;; SEND INPUT TO ROBOT, READ OUTPUT FROM ROBOT
    (search-loop input-ch output-ch state-atom)
    (def end-state @state-atom) ; for part 2
    (@state-atom (@state-atom :result))))

#_
(part1)
;; => 214

;;;;;;;;;;;;;;;;;;;;;
;; PART 2

;; Part 1 walked and recorded the walkable space. It stored final state in "end-state" for convenience.

;; algorithm? for each position, if it can be oxygenated, oxygenate it and recur on all adjacent positions.
;; => not quite, this is depth first in a way that cannot accurately measure the time.
;; For each position, find all neighbors that can have oxygen, oxygenate each one to create new state, then recur on each position with new time. return max time from subproblems.

(defn adjacent-positions
  [[y x]]
  [[(inc y) x]
   [(dec y) x]
   [y (inc x)]
   [y (dec x)]])

(defn can-have-oxygen?
  "walls and previously oxygenated positions cannot have oxygen."
  [state position]
  (> (state position) Integer/MIN_VALUE))

(defn oxygenate-position
  "turns position into a wall for simplicity."
  [state position]
  (assoc state position Integer/MIN_VALUE))

(defn part2
  []
  ((fn time-to-oxygenate
     [state position time]
     (let [oxygenable-neighbors (->> position
                                     (adjacent-positions)
                                     (filter (partial can-have-oxygen? state)))
           new-state (reduce oxygenate-position state oxygenable-neighbors)]
       (if (seq oxygenable-neighbors)
         (->> oxygenable-neighbors
              (map (fn [neighbor]
                     (time-to-oxygenate new-state
                                        neighbor
                                        (inc time))))
              (apply max))
         time)))
   (dissoc end-state :result)
   (:result end-state)
   0))

#_
(part2)
