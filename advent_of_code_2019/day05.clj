(ns day05
  (:require [clojure.edn :as edn]
            [clojure.string :as string]
            [day04]
            [clojure.java.io :as io]))

(defn- p [x] (clojure.pprint/pprint x) x)

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
           (memory (+ pc (inc pos)))
           (memory (memory (+ pc (inc pos))))))
       (range param-count)
       (concat (parameter-modes (memory pc))
               (repeat 0))))

(comment
  (opcode 1002)
  (opcode 99)
  (opcode 3)
  (parameters [1002 4 3 4 33] 0 2)
  (parameter-modes 1002))

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

(defn f
  [memorystr inputs]
  (-> memorystr
      (parse-memory-string)
      (execute inputs)))

#_
(f "3,0,4,0,99" [5]) ; outputs whatever it gets as inputs

#_
(f "1002,4,3,4,33" [])

#_
(f (slurp (io/resource "day05_input.txt")) [1])
;; => [0 0 0 0 0 0 0 0 0 15314507]
;; part 1 correct


;; PART 2

(defn try-20
  [memorystr]
  (->> (range 20)
       (map vector)
       (map (partial f memorystr))))

#_
(try-20 "3,9,8,9,10,9,4,9,99,-1,8")
;; => ([0] [0] [0] [0] [0] [0] [0] [0] [1] [0] [0] [0] [0] [0] [0] [0] [0] [0] [0] [0])

#_
(try-20 "3,9,7,9,10,9,4,9,99,-1,8")
;; => ([1] [1] [1] [1] [1] [1] [1] [1] [0] [0] [0] [0] [0] [0] [0] [0] [0] [0] [0] [0])

#_
(try-20 "3,3,1108,-1,8,3,4,3,99")
;; => ([0] [0] [0] [0] [0] [0] [0] [0] [1] [0] [0] [0] [0] [0] [0] [0] [0] [0] [0] [0])

#_
(try-20 "3,3,1107,-1,8,3,4,3,99")
;; => ([1] [1] [1] [1] [1] [1] [1] [1] [0] [0] [0] [0] [0] [0] [0] [0] [0] [0] [0] [0])

#_
(try-20 "3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9")
;; => ([0] [1] [1] [1] [1] [1] [1] [1] [1] [1] [1] [1] [1] [1] [1] [1] [1] [1] [1] [1])

#_
(try-20 "3,3,1105,-1,9,1101,0,0,12,4,12,99,1")
;; => ([0] [1] [1] [1] [1] [1] [1] [1] [1] [1] [1] [1] [1] [1] [1] [1] [1] [1] [1] [1])

#_
(try-20 "3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99")
;; => ([999] [999] [999] [999] [999] [999] [999] [999] [1000] [1001] [1001] [1001] [1001] [1001] [1001] [1001] [1001] [1001] [1001] [1001])

#_
(f (slurp (io/resource "day05_input.txt")) [5])
;; => [652726]
;; part 2 correct
