(ns day12
  "https://adventofcode.com/2019/day/12"
  (:require [clojure.string :as string]
            [day10]))

(defn parse
  [inputstr]
  (->> (string/split inputstr #"\n")
       (remove empty?)
       (map #(string/split % #","))
       (map (partial map (partial re-find #"-?\d+")))
       (map (partial map #(Long. %)))
       (map #(concat % [0 0 0]))
       (mapv vec)))

(defn update-positions
  "needs to be mapv to reduce stackoverflow in outer lazy seq"
  [moons]
  (mapv (fn [moon]
         (-> moon
             (update 0 + (moon 3))
             (update 1 + (moon 4))
             (update 2 + (moon 5))))
       moons))

(defn compare-positions
  [pos pos-others]
  (reduce (fn [acc elem]
            (cond
              (< elem pos) (dec acc)
              (> elem pos) (inc acc)
              (= elem pos) acc))
          0
          pos-others))

(defn update-velocities
  "needs to be mapv to reduce stackoverflow in outer lazy seq"
  [moons]
  (mapv (fn [moon]
          (let [othermoons (remove #{moon} moons)]
            (-> moon
                (update 3 + (compare-positions (moon 0) (map #(% 0) othermoons)))
                (update 4 + (compare-positions (moon 1) (map #(% 1) othermoons)))
                (update 5 + (compare-positions (moon 2) (map #(% 2) othermoons))))))
        moons))

(defn pos-vels
  [moons]
  (lazy-seq (cons moons
                  (pos-vels (->> moons
                                 (update-velocities)
                                 (update-positions))))))

(defn part1
  [n inputstr]
  (last (take (inc n)
              (pos-vels (parse inputstr)))))

(defn potential-energy
  [moon]
  (->> moon
       (take 3)
       (map #(Math/abs %))
       (apply +)))

(defn kinetic-energy
  [moon]
  (->> moon
       (take-last 3)
       (map #(Math/abs %))
       (apply +)))

(defn total-energy
  [moons]
  (->> moons
       (map (juxt potential-energy kinetic-energy))
       (map (partial apply *))
       (apply +)))

#_
(total-energy (part1 1000
        "
<x=13, y=9, z=5>
<x=8, y=14, z=-2>
<x=-5, y=4, z=11>
<x=2, y=-6, z=1>
"))
;; => 6490

;;;;;;;;;;;;
;; PART 2
;;;;;;;;;;;;

;; find the cycle for each moon for each of x y z p,v... then find the lcm of all cycles

(def moon-step (comp update-positions update-velocities))

(defn lcm
  ([x y]
   (/ (* x y)
      (day10/gcf x y)))
  ([x y & args]
   (reduce lcm (concat [x y] args))))

(let [pred (fn [idx [moon init]] (= [(init idx) (init (+ idx 3))]
                                    [(moon idx) (moon (+ idx 3))]))]
  (defn cycle?
    "idx corresponds to x y z"
    [idx moons inits cycles]
    (and (not (cycles idx))
         (->> moons
              (map vector inits)
              (every? (partial pred idx))))))

(defn part2
  [inputstr]
  (let [initial (parse inputstr)]
    (loop [stepnum 1
           moons (moon-step initial)
           xyz-cycles [nil nil nil]]
      (if (not-any? nil? xyz-cycles)
        (apply lcm xyz-cycles)
        (recur (inc stepnum)
               (moon-step moons)
               (cond-> xyz-cycles
                 (cycle? 0 moons initial xyz-cycles) (assoc 0 stepnum) ; x
                 (cycle? 1 moons initial xyz-cycles) (assoc 1 stepnum) ; y
                 (cycle? 2 moons initial xyz-cycles) (assoc 2 stepnum)))))))

#_
(part2
 "
<x=13, y=9, z=5>
<x=8, y=14, z=-2>
<x=-5, y=4, z=11>
<x=2, y=-6, z=1>
")
;; => 277068010964808
