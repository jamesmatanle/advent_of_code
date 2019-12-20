(ns day01
  (:require [clojure.test :as t]))
;; https://adventofcode.com/2019

;; debug fn
(defn p [x] (println x) x)

;;;;;;;;;;;;
;; DAY 01
;; Given a list of modules, determine total fuel needs.
;;;;;;;;;;;;

(def input
  "copied from https://adventofcode.com/2019/day/1/input"
  [78390 73325 52095 126992 106546 81891 69484 131138
   95103 53296 115594 79485 130202 95238 99332 136331
   124321 127271 108047 69186 90597 96001 138773 55284
   127936 110780 89949 85360 55470 110124 101201 139745
   148170 149108 79579 139733 52014 125910 77323 145751
   52161 105606 132240 69907 144129 116958 60818 144964
   111789 85657 115509 84509 50702 69012 110376 134213
   127319 92966 58422 144491 128198 84367 94269 147895
   105494 88369 117882 146239 50704 62591 149258 63118
   145393 122997 136534 96402 121057 59561 86916 75873
   68670 147465 62902 145858 137810 108108 97314 118001
   54699 56603 66821 80744 124514 143113 132581 79376
   105728 115337 111028 52209])

(defn fuel-quantity
  "returns necessary fuel quanity for module of mass m."
  [m]
  (-> m
      (/ 3)
      (Math/floor)
      (long)
      (- 2)))

(mapv fuel-quantity
      [12 14 1969 100756]) ; => [2 2 654 33583]

(->> input
     (mapv fuel-quantity)
     (reduce +))

;; => 3391707
;; correct

;;;;;;;;;;;;
;; PART 2
;; Fuel also requires fuel
;;;;;;;;;;;;

(defn fuel-quantity-2
  [m]
  (let [fq (fuel-quantity m)]
    (if (<= 0 fq)
      (+ fq (fuel-quantity-2 fq))
      0)))

(fuel-quantity-2 14) ; => 2
(fuel-quantity-2 1969) ; => 966
(fuel-quantity-2 100756) ; => 50346

(->> input
     (mapv fuel-quantity-2)
     (reduce +))

;; => 5084676
;; correct
