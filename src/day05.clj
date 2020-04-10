(ns day05
  (:require [clojure.edn :as edn]
            [clojure.string :as string]
            [day04]
            [clojure.java.io :as io]))

(defn opcode
  [instruction]
  (mod instruction 100))

(defn parameter-modes
  [instruction]
  (->> instruction
       (day04/parse-to-sequence)
       (reverse)
       (drop 2)))

(defn parameters
  [memory pc param-count]
  (map (fn [pos mode]
         (if (= 1 mode)
           (memory (+ pc pos))
           (memory (memory (+ pc pos)))))
       (map inc (range param-count))
       (concat (parameter-modes (memory pc))
               (repeat 0))))

(defn pc-jump-if
  [memory pc pred]
  (let [parameters (parameters memory pc 2)]
    (if (pred 0 (first parameters))
      (second parameters)
      (+ pc 3))))

(defn pred-test
  [memory pc pred]
  (let [parameters (parameters memory pc 2)]
    (assoc memory
           (memory (+ pc 3))
           (if (pred (first parameters)
                     (second parameters))
             1
             0))))

(defn execute
  "NOTE can tail call optimize... not necessary though..."
  ([memory inputs]
   (execute memory inputs [] 0))
  ([memory inputs outputs pc]
   (case (opcode (memory pc))
     1 (execute (assoc memory
                       (memory (+ pc 3)) ; storage parameter cannot be immediate.
                       (apply + (parameters memory pc 2)))
                inputs
                outputs
                (+ pc 4))
     2 (execute (assoc memory
                       (memory (+ pc 3))
                       (apply * (parameters memory pc 2)))
                inputs
                outputs
                (+ pc 4))
     3 (execute (assoc memory
                       (memory (+ pc 1))
                       (first inputs))
                (rest inputs)
                outputs
                (+ pc 2))
     4 (execute memory
                inputs
                (apply conj outputs (parameters memory pc 1))
                (+ pc 2))
     5 (execute memory
                inputs
                outputs
                (pc-jump-if memory pc not=))
     6 (execute memory
                inputs
                outputs
                (pc-jump-if memory pc =))
     7 (execute (pred-test memory pc <)
                inputs
                outputs
                (+ pc 4))
     8 (execute (pred-test memory pc =)
                inputs
                outputs
                (+ pc 4))
     99 outputs
     (throw (ex-info "bad opcode" {:x [memory pc]})))))

(defn parse-memory-string
  [memorystr]
  (->> (string/split memorystr #",")
       (map edn/read-string)
       (vec)))

(defn part1
  [memorystr inputs]
  (-> memorystr
      (parse-memory-string)
      (execute inputs)))

#_
(part1 (util/fstr "day05_input.txt") [1])
;; => [0 0 0 0 0 0 0 0 0 15314507]

;; PART 2

(defn try-20
  [memorystr]
  (->> (range 20)
       (map vector)
       (map (partial part1 memorystr))))

#_
(part1 (slurp (io/resource "day05_input.txt")) [5])
;; => [652726]
