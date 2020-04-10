(ns day07
  (:require [day05]
            [clojure.math.combinatorics :as combo]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.string :as string]
            [clojure.core.async :as async]))

;; call day05/part1 for each of 5 thrusters, provide each a parameter and previous output (0 for first thruster.)
;; parameter can be 0-4 used exactly once...5! = 120 combinations to try.

(defn try-input-settings
  [inputstr inputs]
  (reduce (fn [prev-output input] ; returns output
            (first (day05/part1 inputstr [input prev-output])))
          0
          inputs))

(defn part1
  [inputstr]
  (->> (combo/permutations #{0 1 2 3 4})
       (map (partial try-input-settings inputstr))
       (apply max)))

#_
(part1 (util/fstr "day07_input.txt"))
;; => 34686


;;;;;;;;;;;;
;; PART 2
;;;;;;;;;;;;
;; need to rewrite my computer such that it can accept input while running. that is, as soon as one machine creates output, send it to the next machine. after 99, check final output.

(defn execute
  "NOTE can tail call optimize... not necessary though..."
  ([memory input output]
   ((fn [memory pc]
      (case (day05/opcode (memory pc))
        1 (recur (assoc memory
                        (memory (+ pc 3)) ; storage parameter cannot be immediate.
                        (apply + (day05/parameters memory pc 2)))
                 (+ pc 4))
        2 (recur (assoc memory
                        (memory (+ pc 3))
                        (apply * (day05/parameters memory pc 2)))
                 (+ pc 4))
        3 (recur (assoc memory
                        (memory (+ pc 1))
                        (async/<!! input))
                 (+ pc 2))
        4 (do (async/>!! output
                         (first (day05/parameters memory pc 1)))
              (recur memory
                     (+ pc 2)))
        5 (recur memory
                 (day05/pc-jump-if memory pc not=))
        6 (recur memory
                 (day05/pc-jump-if memory pc =))
        7 (recur (day05/pred-test memory pc <)
                 (+ pc 4))
        8 (recur (day05/pred-test memory pc =)
                 (+ pc 4))
        99 nil
        (throw (ex-info "bad opcode" {:x [memory pc]}))))
    memory 0)))

(defn try-input-settings-2
  [memory settings]
  (let [ch0 (async/chan 100)
        ch1 (async/chan 100)
        ch2 (async/chan 100)
        ch3 (async/chan 100)
        ch4 (async/chan 100)]
    (async/>!! ch0 (nth settings 0))
    (async/>!! ch1 (nth settings 1))
    (async/>!! ch2 (nth settings 2))
    (async/>!! ch3 (nth settings 3))
    (async/>!! ch4 (nth settings 4))
    (async/>!! ch0 0)
    (future (execute memory ch0 ch1))
    (future (execute memory ch1 ch2))
    (future (execute memory ch2 ch3))
    (future (execute memory ch3 ch4))
    (execute memory ch4 ch0) ; BLOCK so that the following take is the final one...
    (async/<!! ch0)))

(defn part2
  [inputstr]
  (->> (combo/permutations [5 6 7 8 9])
       (map (partial try-input-settings-2 (day05/parse-memory-string inputstr)))
       (apply max)))
#_
(part2 (util/fstr "day07_input.txt"))
;; => 36384144
