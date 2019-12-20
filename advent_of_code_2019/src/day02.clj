(ns day02
  (:require [clojure.string :as string]
            [clojure.edn :as edn]))

(defn- p [x] (clojure.pprint/pprint x) x)

(def input
  "https://adventofcode.com/2019/day/2/input"
  "1,0,0,3,1,1,2,3,1,3,4,3,1,5,0,3,2,1,9,19,1,19,5,23,2,23,13,27,1,10,27,31,2,31,6,35,1,5,35,39,1,39,10,43,2,9,43,47,1,47,5,51,2,51,9,55,1,13,55,59,1,13,59,63,1,6,63,67,2,13,67,71,1,10,71,75,2,13,75,79,1,5,79,83,2,83,9,87,2,87,13,91,1,91,5,95,2,9,95,99,1,99,5,103,1,2,103,107,1,10,107,0,99,2,14,0,0")

(def input-fixed
  "1,12,2,3,1,1,2,3,1,3,4,3,1,5,0,3,2,1,9,19,1,19,5,23,2,23,13,27,1,10,27,31,2,31,6,35,1,5,35,39,1,39,10,43,2,9,43,47,1,47,5,51,2,51,9,55,1,13,55,59,1,13,59,63,1,6,63,67,2,13,67,71,1,10,71,75,2,13,75,79,1,5,79,83,2,83,9,87,2,87,13,91,1,91,5,95,2,9,95,99,1,99,5,103,1,2,103,107,1,10,107,0,99,2,14,0,0")

(defn arithmetic
  [memory i f]
  (assoc memory
         (memory (+ i 3))
         (f (memory (memory (+ i 1)))
            (memory (memory (+ i 2))))))

(defn execute
  "NOTE can tail call optimize... not necessary though..."
  ([memory]
   (execute memory 0))
  ([memory pc]
   (cond
     (= 1 (memory pc)) (execute (arithmetic memory pc +)
                                (+ pc 4))
     (= 2 (memory pc)) (execute (arithmetic memory pc *)
                                (+ pc 4))
     (= 99 (memory pc)) memory
     :else (throw (ex-info "bad opcode" {})))))

(defn parse-memory-string
  [input]
  (->> (string/split input #",")
       (map edn/read-string)
       (vec)))

(defn f
  [memorystr]
  (->> memorystr
       (parse-memory-string)
       (execute)
       (string/join ",")))

(f "1,0,0,0,99") ;; => 2,0,0,0,99 (1 + 1 = 2).
(f "2,3,0,3,99") ;; => 2,3,0,6,99 (3 * 2 = 6).
(f "2,4,4,5,99,0") ;; => 2,4,4,5,99,9801 (99 * 99 = 9801).
(f "1,1,1,4,99,5,6,0,99") ;; => 30,1,1,4,2,5,6,0,99.

(f input-fixed)
;; => "3895705,12,2,2,1,1,2,3,1,3,4,3,1,5,0,3,2,1,9,36,1,19,5,37,2,23,13,185,1,10,27,189,2,31,6,378,1,5,35,379,1,39,10,383,2,9,43,1149,1,47,5,1150,2,51,9,3450,1,13,55,3455,1,13,59,3460,1,6,63,3462,2,13,67,17310,1,10,71,17314,2,13,75,86570,1,5,79,86571,2,83,9,259713,2,87,13,1298565,1,91,5,1298566,2,9,95,3895698,1,99,5,3895699,1,2,103,3895701,1,10,107,0,99,2,14,0,0"

;; correct


;;;;;;;;;;;;
;;;;;;;;;;;;
;; PART 2
;; What values at addresses 1 and 2 (from 0-99) produce target 19690720 in address 0?
;;;;;;;;;;;;

(defn execute-2
  "NOTE can tail call optimize... not necessary though..."
  ([memory target]
   (execute-2 memory target 0))
  ([memory target pc]
   ((fn [memory pc]
      (cond
        (< target (memory 0)) memory ; can backtrack if exceeds target because operations can only increase value.
        (= 1 (memory pc)) (execute (arithmetic memory pc +)
                                   (+ pc 4))
        (= 2 (memory pc)) (execute (arithmetic memory pc *)
                                   (+ pc 4))
        (= 99 (memory pc)) memory
        :else (throw (ex-info "bad opcode" {}))))
    memory pc)))

(defn cartesian-product
  [xs ys]
  (for [x xs y ys]
    [x y]))

(defn make-candidate
  [memory [x y]]
  (assoc memory 1 x 2 y))

(defn f2
  [memorystr target]
  (let [memory (parse-memory-string memorystr)]
    (reduce (fn [_ xy]
              (let [result-memory (execute-2 (make-candidate memory xy) target)]
                (when (= target (result-memory 0))
                  (reduced xy))))
            nil
            (cartesian-product (range 100) (range 100)))))

(f2 input 19690720) ; => [64 17]

(+ (* 100 64) 17)
