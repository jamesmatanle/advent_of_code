(ns day12
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

(defn f
  [n inputstr]
  (last (take (inc n)
              (pos-vels (parse inputstr)))))

#_
(parse
 "
<x=-1, y=0, z=2>
<x=2, y=-10, z=-7>
<x=4, y=-8, z=8>
<x=3, y=5, z=-1>")

#_
(f 10
   "
<x=-1, y=0, z=2>
<x=2, y=-10, z=-7>
<x=4, y=-8, z=8>
<x=3, y=5, z=-1>")
;; => ([2 1 -3 -3 -2 1] [1 -8 0 -1 1 3] [3 -6 1 3 2 -3] [2 0 4 1 -1 -1])


#_
(f 100
 "
<x=-8, y=-10, z=0>
<x=5, y=5, z=10>
<x=2, y=-7, z=3>
<x=9, y=-8, z=-3>")
;; => ([8 -12 -9 -7 3 0] [13 16 -3 3 -11 -5] [-29 -11 -1 -3 7 4] [16 -13 23 7 1 1])

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
(total-energy (f 100
                 "
<x=-8, y=-10, z=0>
<x=5, y=5, z=10>
<x=2, y=-7, z=3>
<x=9, y=-8, z=-3>"))
;; => 1940

#_
(total-energy (f 1000
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

#_
(lcm 80 123 214 75 60) ; => 300

(let [pred (fn [idx [moon init]] (= [(init idx) (init (+ idx 3))]
                                    [(moon idx) (moon (+ idx 3))]))]
  (defn cycle?
    "idx corresponds to x y z"
    [idx moons inits cycles]
    (and (not (cycles idx))
         (->> moons
              (map vector inits)
              (every? (partial pred idx))))))

(defn f2
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
(f2
   "
<x=-1, y=0, z=2>
<x=2, y=-10, z=-7>
<x=4, y=-8, z=8>
<x=3, y=5, z=-1>
")
;; => 2772

#_
(f2 "
<x=-8, y=-10, z=0>
<x=5, y=5, z=10>
<x=2, y=-7, z=3>
<x=9, y=-8, z=-3>
")
;; => 4686774924

#_
(f2
   "
<x=13, y=9, z=5>
<x=8, y=14, z=-2>
<x=-5, y=4, z=11>
<x=2, y=-6, z=1>
")
;; => 277068010964808
