(ns day09
  "https://adventofcode.com/2019/day/9"
  (:require [clojure.edn :as edn]
            [day04]
            [day05]
            [clojure.string :as string]
            [clojure.java.io :as io]
            [clojure.core.async :as async]))

;; built off of day 7.

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
       (concat (day05/parameter-modes (memory pc))
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
  [memory in out]
  (loop [memory memory
         pc 0
         rb 0]
    (case (day05/opcode (memory pc))
      1 (recur (arithmetic memory pc rb +)
               (+ pc 4)
               rb)
      2 (recur (arithmetic memory pc rb *)
               (+ pc 4)
               rb)
      3 (recur (assoc memory
                      (first (parameters memory pc rb 1))
                      ((async/<!! in)))
               (+ pc 2)
               rb)
      4 (do (async/>!! out (->> (parameters memory pc rb 1) first (mget memory)))
            (recur memory
                   (+ pc 2)
                   rb))
      5 (recur memory
               (pc-jump-if memory pc rb not=)
               rb)
      6 (recur memory
               (pc-jump-if memory pc rb =)
               rb)
      7 (recur (pred-test memory pc rb <)
               (+ pc 4)
               rb)
      8 (recur (pred-test memory pc rb =)
               (+ pc 4)
               rb)
      9 (recur memory
               (+ pc 2)
               (+ rb (mget memory (first (parameters memory pc rb 1)))))
      99 (do (async/close! in) (async/close! out))
      (throw (ex-info "bad opcode" {:x [memory pc]})))))

(defn parse-memory-string
  [memorystr]
  (->> (string/split memorystr #",")
       (map edn/read-string)
       (zipmap (range))
       (into (sorted-map))))

(defn execute-string
  [memorystr in out]
  (execute (parse-memory-string memorystr) in out))

(defn part1
  [memorystr inputs]
  (let [in (async/to-chan (map (fn [i] (fn [] i))
                               inputs))
        out (async/chan 99999)]
    (execute-string memorystr in out)
    (async/<!! (async/into [] out))))

#_
(part1 (slurp (io/resource "day09_input.txt")) [1])
;; => [3409270027]

;;;;;;;;;;;;
;; PART 2
;;;;;;;;;;;;

#_
(part1 (slurp (io/resource "day09_input.txt")) [2])
;; => [82760]
;; finally had to tail call optimize :O

