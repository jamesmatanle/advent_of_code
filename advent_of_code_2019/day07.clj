(ns day07
  (:require [day05]
            [clojure.math.combinatorics :as combo]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.string :as string]
            [clojure.core.async :as async]
            [clojure.tools.logging :as log]))

(defn- p [x] (clojure.pprint/pprint x))
;; call day05/f for each of 5 thrusters, provide each a parameter and previous output (0 for first thruster.)
;; parameter can be 0-4 used exactly once...5! = 120 combinations to try.

(defn try-input-settings
  [inputstr inputs]
  (reduce (fn [prev-output input] ; returns output
            (first (day05/f inputstr [input prev-output])))
          0
          inputs))

(defn f
  [inputstr]
  (->> (combo/permutations #{0 1 2 3 4})
       (map (partial try-input-settings inputstr))
       (apply max)))

#_
(try-input-settings "3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0" [4 3 2 1 0])
;; => 43210
#_
(f "3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0")
;; => 43210


#_
(try-input-settings "3,23,3,24,1002,24,10,24,1002,23,-1,23,101,5,23,23,1,24,23,23,4,23,99,0,0" [0 1 2 3 4])
;; => 54321

#_
(f "3,23,3,24,1002,24,10,24,1002,23,-1,23,101,5,23,23,1,24,23,23,4,23,99,0,0")
;; => 54321


#_
(try-input-settings "3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0" [1 0 4 3 2])
;; => 65210

#_
(f "3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0")
;; => 65210


#_
(f (slurp (io/resource "day07_input.txt")))
;; => 34686 correct


;;;;;;;;;;;;
;; PART 2
;;;;;;;;;;;;
;; need to rewrite my computer such that it can accept input while running. that is, as soon as one machine creates output, send it to the next machine. after 99, check final output.

(defn computer
  "NOTE can tail call optimize... not necessary though..."
  ([memory]
   (let [inputatom (atom nil)
         outputatom (atom nil)]
     (fn [x]
       (case x
         "set-input!" (fn [input] (reset! inputatom input))
         "set-output!" (fn [output] (reset! outputatom output))
         "execute"
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
                              (async/<!! @inputatom))
                       (+ pc 2))
              4 (do (async/>!! @outputatom
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
          memory 0))))))

(defn computer-execute!
  [computer]
  (computer "execute"))

(defn computer-set-input!
  [computer input]
  ((computer "set-input!") input))

(defn computer-set-output!
  [computer output]
  ((computer "set-output!") output))

(defn try-input-settings-2
  [memory settings]
  (let [ch0 (async/chan 100)
        ch1 (async/chan 100)
        ch2 (async/chan 100)
        ch3 (async/chan 100)
        ch4 (async/chan 100)
        a (computer memory)
        b (computer memory)
        c (computer memory)
        d (computer memory)
        e (computer memory)]
    (computer-set-input! a ch0)
    (computer-set-output! a ch1)
    (computer-set-input! b ch1)
    (computer-set-output! b ch2)
    (computer-set-input! c ch2)
    (computer-set-output! c ch3)
    (computer-set-input! d ch3)
    (computer-set-output! d ch4)
    (computer-set-input! e ch4)
    (computer-set-output! e ch0)
    (async/>!! ch0 (nth settings 0))
    (async/>!! ch1 (nth settings 1))
    (async/>!! ch2 (nth settings 2))
    (async/>!! ch3 (nth settings 3))
    (async/>!! ch4 (nth settings 4))
    (async/>!! ch0 0)
    (future (computer-execute! a))
    (future (computer-execute! b))
    (future (computer-execute! c))
    (future (computer-execute! d))
    (computer-execute! e) ; BLOCK so that the following take is the final one...
    (async/<!! ch0)))

(defn f2
  [inputstr]
  (->> (combo/permutations [5 6 7 8 9])
       (map (partial try-input-settings-2 (day05/parse-memory-string inputstr)))
       (apply max)))

#_
(f2 "3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5")
;; => 139629729

#_
(f2 "3,52,1001,52,-5,52,3,53,1,52,56,54,1007,54,5,55,1005,55,26,1001,54,-5,54,1105,1,12,1,53,54,53,1008,54,0,55,1001,55,1,55,2,53,55,53,4,53,1001,56,-1,56,1005,56,6,99,0,0,0,0,10")
;; => 18216

#_
(f2 (slurp (io/resource "day07_input.txt")))
;; => 36384144
;; correct
