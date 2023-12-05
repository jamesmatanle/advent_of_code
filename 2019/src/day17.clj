(ns day17
  "https://adventofcode.com/2019/day/17"
  (:require [util]
            [day09]
            [clojure.core.async :as async]
            [clojure.string :as string]))

(defn take-outputs
  [output-ch]
  (loop [outputs []]
    (let [v (async/<!! output-ch)]
      (if (nil? v)
        outputs
        (recur (conj outputs v))))))

(defn get-view
  []
  (let [output-ch (async/chan 100000)
        ;; ROBOT ON INTCODE COMPUTER
        fut (future
              (try
                (day09/execute-string (util/fstr "day17_input.txt") (async/chan) output-ch)
                (catch Exception e
                  (println e))))
        res (take-outputs output-ch)]
    (future-cancel fut)
    res))

(def view
  (->> (get-view)
       (map char)
       (apply str)
       (string/split-lines)
       (mapv vec)))

(defn intersection?
  [view row col]
  (every? #(= \# (get-in view %))
          [[row col]
           [(inc row) col]
           [(dec row) col]
           [row (inc col)]
           [row (dec col)]]))

(defn part1
  [view]
  (apply + (for [row (-> view count range butlast rest)
                 col (-> row view count range butlast rest)]
             (if (intersection? view row col)
               (* row col)
               0))))

#_
(part1 view)
;; => 3920

;;;;;;;;;
;; PART 2

(def part-2-input
  (-> "day17_input.txt"
      (util/fstr)
      (vec)
      (assoc 0 \2)
      ((partial apply str))))

;; do by hand...
