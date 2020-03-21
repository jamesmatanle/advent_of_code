(ns day16
  (:require [util]
            [day04]))

(def pattern [0 1 0 -1])

(defn digit-pattern
  [digit]
  (->> pattern (mapcat (partial repeat digit)) (cycle) (rest)))

(defn last-digit
  [x]
  (-> x (Math/abs) (mod 10)))

(defn fft
  [xs]
  (map (fn [digit]
         (->> xs
              (map vector (digit-pattern (inc digit)))
              (reduce (fn [acc [pattern-digit x]]
                        (+ acc (* pattern-digit x)))
                      0)
              (last-digit)))
       (range (count xs))))

(defn fft-phases
  [phases xs]
  (reduce (fn [acc _] (fft acc))
          xs
          (range phases)))

(defn part1
  [phases inputstr]
  (->> inputstr
       (day04/parse-to-sequence)
       (fft-phases phases)
       (take 8)))

#_
(part1 100 (util/fstr "day16_input.txt"))
;; => (3 2 0 0 2 8 3 5)

;; PART 2 big input
;; Is part1 fast enough for x10000 increase in number of digits? No. ~6500000^2 * 100
;; Leverage offset for fast hack part2 answer
;; Output after offset only relies on input after offset for a phase.
;; Offset is in second half. without need to calculate first half of digits pattern reduces to 0 .. 0 1 .. 1
;; Each digit is itself plus sum of following digits.

(defn fft-part2
  [xs]
  (loop [sum 0
         res '()
         xs (reverse xs)]
    (if (empty? xs)
      (conj res sum)
      (recur (last-digit (+ sum (first xs)))
             (conj res sum)
             (rest xs)))))

(defn drop-offset
  [digits]
  (drop (Long/valueOf (apply str (take 7 digits)))
        digits))

(defn part2
  [phases inputstr]
  (with-redefs [fft fft-part2]
    (->> inputstr
         (repeat 10000)
         (apply str)
         (day04/parse-to-sequence)
         (drop-offset)
         (fft-phases phases)
         (take 8))))

#_
(part2 100 (util/fstr "day16_input.txt"))
;; => (6 9 7 3 2 2 6 8)
