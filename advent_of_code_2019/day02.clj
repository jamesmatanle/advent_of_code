(ns day02
  (:require [clojure.string :as string]
            [clojure.edn :as edn]))

(defn- p [x] (println x) x)

(def input
  "1,0,0,3,1,1,2,3,1,3,4,3,1,5,0,3,2,1,9,19,1,19,5,23,2,23,13,27,1,10,27,31,2,31,6,35,1,5,35,39,1,39,10,43,2,9,43,47,1,47,5,51,2,51,9,55,1,13,55,59,1,13,59,63,1,6,63,67,2,13,67,71,1,10,71,75,2,13,75,79,1,5,79,83,2,83,9,87,2,87,13,91,1,91,5,95,2,9,95,99,1,99,5,103,1,2,103,107,1,10,107,0,99,2,14,0,0")

(def input-fixed
  "1,12,2,3,1,1,2,3,1,3,4,3,1,5,0,3,2,1,9,19,1,19,5,23,2,23,13,27,1,10,27,31,2,31,6,35,1,5,35,39,1,39,10,43,2,9,43,47,1,47,5,51,2,51,9,55,1,13,55,59,1,13,59,63,1,6,63,67,2,13,67,71,1,10,71,75,2,13,75,79,1,5,79,83,2,83,9,87,2,87,13,91,1,91,5,95,2,9,95,99,1,99,5,103,1,2,103,107,1,10,107,0,99,2,14,0,0")

(defn operate!
  [memory-atom i f]
  (swap! memory-atom
         assoc
         (@memory-atom (+ i 3))
         (f (@memory-atom (@memory-atom (+ i 1)))
            (@memory-atom (@memory-atom (+ i 2))))))

(defn execute-program!
  [memory-atom]
  ((fn [i]
     (if (= i (dec (count @memory-atom)))
       memory-atom
       (cond
         (= 1 (@memory-atom i)) (do (operate! memory-atom i +)
                                    (recur (+ i 4)))
         (= 2 (@memory-atom i)) (do (operate! memory-atom i *)
                                    (recur (+ i 4)))
         (= 99 (@memory-atom i)) "done"
         :else (throw (ex-info {:msg "bad data" :i i})))))
   0))

(defn parse-input
  [input]
  (->> (string/split input #",")
       (map edn/read-string)
       (vec)))

(defn parse-and-eval
  [input]
  (let [res-atom (atom (parse-input input))]
    (execute-program! res-atom)
    (string/join "," @res-atom)))

(parse-and-eval "1,0,0,0,99") ;; => 2,0,0,0,99 (1 + 1 = 2).
(parse-and-eval "2,3,0,3,99") ;; => 2,3,0,6,99 (3 * 2 = 6).
(parse-and-eval "2,4,4,5,99,0") ;; => 2,4,4,5,99,9801 (99 * 99 = 9801).
(parse-and-eval "1,1,1,4,99,5,6,0,99") ;; => 30,1,1,4,2,5,6,0,99.

(parse-and-eval input-fixed)
;; => "3895705,12,2,2,1,1,2,3,1,3,4,3,1,5,0,3,2,1,9,36,1,19,5,37,2,23,13,185,1,10,27,189,2,31,6,378,1,5,35,379,1,39,10,383,2,9,43,1149,1,47,5,1150,2,51,9,3450,1,13,55,3455,1,13,59,3460,1,6,63,3462,2,13,67,17310,1,10,71,17314,2,13,75,86570,1,5,79,86571,2,83,9,259713,2,87,13,1298565,1,91,5,1298566,2,9,95,3895698,1,99,5,3895699,1,2,103,3895701,1,10,107,0,99,2,14,0,0"

;; correct


;;;;;;;;;
;;;;;;;;;
;; PART 2: how can value at addresses 1 and 2 vary betwen 0 and 99 to produce target 19690720 in address 0?
;;;;;;;;;

;; backtrack: if memory location 0 exceeds target 19690720, try a different pair. backtrack after executing every instruction.

(defn execute-program-2!
  [memory-atom target]
  ((fn [i]
     (if (= i (dec (count @memory-atom)))
       memory-atom
       (cond
         (< target (@memory-atom 0)) "value too large" ; backtrack
         (= 1 (@memory-atom i)) (do (operate! memory-atom i +)
                                    (recur (+ i 4)))
         (= 2 (@memory-atom i)) (do (operate! memory-atom i *)
                                    (recur (+ i 4)))
         (= 99 (@memory-atom i)) "done"
         :else (throw (ex-info {:msg "bad data" :i i})))))
   0))

(defn part-02
  [program target]
  (let [res-atom (atom [nil])]
    (last (for [i (range 100)
                j (range 100)
                :while (not= target (@res-atom 0))]
            (do (reset! res-atom (parse-input program))
                (swap! res-atom assoc 1 i 2 j)
                (execute-program-2! res-atom target))))
    @res-atom))

(part-02 input 19690720)
;; => [19690720 64 17 2 1 1 2 3 1 3 4 3 1 5 0 3 2 1 9 192 1 19 5 193 2 23 13 965 1 10 27 969 2 31 6 1938 1 5 35 1939 1 39 10 1943 2 9 43 5829 1 47 5 5830 2 51 9 17490 1 13 55 17495 1 13 59 17500 1 6 63 17502 2 13 67 87510 1 10 71 87514 2 13 75 437570 1 5 79 437571 2 83 9 1312713 2 87 13 6563565 1 91 5 6563566 2 9 95 19690698 1 99 5 19690699 1 2 103 19690716 1 10 107 0 99 2 14 0 0]

(+ (* 100 64) 17)
