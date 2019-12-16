(ns day09
  "https://adventofcode.com/2019/day/9"
  (:require [clojure.edn :as edn]
            [day04]
            [clojure.string :as string]
            [clojure.java.io :as io]))

(defn p [x] (clojure.pprint/pprint x) x)

;; built off of day 5 rather than 7 to have simpler functional I/O

(defn opcode
  [instruction]
  (mod instruction 100))

(defn parameter-modes
  [instruction]
  (->> instruction
       (day04/parse-to-sequence)
       (reverse)
       (drop 2)))

(defn mget
  [m x]
  (m x 0))

(defn parameters
  [memory pc rb param-count]
  (map (fn [pos mode]
         (case mode
           2 (+ rb (memory (+ pc pos))) ; relative
           1 (+ pc pos) ; immediate
           0 (memory (+ pc pos)) ; position
           (throw (ex-info "bad mode" {}))))
       (map inc (range param-count))
       (concat (parameter-modes (memory pc))
               (repeat 0))))

(defn pc-jump-if
  [memory pc rb pred]
  (let [params (parameters memory pc rb 2)]
    (if (pred 0 (mget memory (first params)))
      (mget memory (second params))
      (+ pc 3))))

(defn pred-test
  [memory pc rb pred]
  (let [params (parameters memory pc rb 3)]
    (assoc memory
           (last params)
           (if (pred (mget memory (first params))
                     (mget memory (second params)))
             1
             0))))

(defn arithmetic
  [memory pc rb op]
  (let [params (parameters memory pc rb 3)]
    (assoc memory
           (last params)
           (->> params
                (take 2)
                (map (partial mget memory))
                (apply op)))))

(defn execute
  [memory inputs]
  (loop [memory memory
         inputs inputs
         outputs []
         pc 0
         rb 0]
    (case (opcode (memory pc))
      1 (recur (arithmetic memory pc rb +)
               inputs
               outputs
               (+ pc 4)
               rb)
      2 (recur (arithmetic memory pc rb *)
               inputs
               outputs
               (+ pc 4)
               rb)
      3 (recur (assoc memory
                      (first (parameters memory pc rb 1))
                      (first inputs))
               (rest inputs)
               outputs
               (+ pc 2)
               rb)
      4 (recur memory
               inputs
               (conj outputs (mget memory
                                   (first (parameters memory pc rb 1))))
               (+ pc 2)
               rb)
      5 (recur memory
               inputs
               outputs
               (pc-jump-if memory pc rb not=)
               rb)
      6 (recur memory
               inputs
               outputs
               (pc-jump-if memory pc rb =)
               rb)
      7 (recur (pred-test memory pc rb <)
               inputs
               outputs
               (+ pc 4)
               rb)
      8 (recur (pred-test memory pc rb =)
               inputs
               outputs
               (+ pc 4)
               rb)
      9 (recur memory
               inputs
               outputs
               (+ pc 2)
               (+ rb (mget memory (first (parameters memory pc rb 1)))))
      99 outputs
      (throw (ex-info "bad opcode" {:x [memory pc]})))))

(defn parse-memory-string
  [memorystr]
  (->> (string/split memorystr #",")
       (map edn/read-string)
       (zipmap (range))
       (into (sorted-map))))

(defn f
  [memorystr inputs]
  (-> memorystr
      (parse-memory-string)
      (execute inputs)))

#_
(f "109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99" [])
;; => [109 1 204 -1 1001 100 1 100 1008 100 16 101 1006 101 0 99]

#_
(->> (f "1102,34915192,34915192,7,4,7,99,0" [])
     (map day04/parse-to-sequence)
     (map count))
;; => [16]

#_
(f "104,1125899906842624,99" [])
;; =>  [1125899906842624]

#_
(f (slurp (io/resource "day09_input.txt")) [1])
;; => [3409270027]
;; correct

;;;;;;;;;;;;
;; PART 2
;;;;;;;;;;;;

#_
(f (slurp (io/resource "day09_input.txt")) [2])
;; => [82760]
;; correct
;; had to tail call optimize :O
