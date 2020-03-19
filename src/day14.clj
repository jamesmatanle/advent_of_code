(ns day14
  "https://adventofcode.com/2019/day/14"
  (:require [clojure.string :as string]
            [clojure.edn :as edn]
            [util]))

(defn maybe-parse-number
  [s]
  (if (re-find #"^\d+$" s)
    (edn/read-string s)
    s))

(defn parse
  [inputstr]
  (->> (string/split inputstr #"\n")
       (remove empty?)
       (map #(string/split % #", | => | "))
       (map reverse)
       (map (partial mapv maybe-parse-number))
       (map (partial partition-all 2))
       (map (fn [formula]
              {(vec (first formula))
               (->> (rest formula)
                    (mapv vec)
                    (into {}))}))
       (apply merge)))

(defn quantity-formula-maps
  [m]
  [(->> m
        (map (fn [[[e q] formula]]
               [e q]))
        (into {}))
   (->> m
        (map (fn [[[e q] formula]]
               [e formula]))
        (into {}))])

;; questions:
;; do formulae ever produce more than one output? no
;; are there ever several ways to produce the same element? no

;; Element X can be required across subproblems. ie, if any subproblem creates extra X, another subproblem must be able to use it.
;; ATTEMPT 1: not purely functional, share extra production across subproblems.

(defn ore-quantity-helper
  [num-fuel [quantities formulas]]
  (let [extras (atom (zipmap (keys quantities) (repeat 0)))]
    ((fn g [element required-quantity]
       (if (= "ORE" element)
         required-quantity
         (let [new-required-quantity (- required-quantity (@extras element))
               formula-repetitions (long (Math/ceil (/ new-required-quantity (quantities element))))]
           (swap! extras update element + (- required-quantity) (* formula-repetitions
                                                                   (quantities element)))
           (apply + (map (fn [[ingredient required-ingredient-quantity]]
                           (g ingredient
                              (* formula-repetitions
                                 required-ingredient-quantity)))
                         (formulas element))))))
     "FUEL" num-fuel)))

(defn ore-per-fuel
  [inputstr]
  (->> inputstr
       (parse)
       (quantity-formula-maps)
       (ore-quantity-helper 1)))

#_
(ore-per-fuel (util/fstr "day14_input.txt"))
;; => 178154

;; Can I do part1 purely functionally? would be messier without reaching across subproblems for extras

;;;;;;;;;;
;; PART 2: max fuel given a trillion ore units.

;; one naive approach would be to generate all possible fuel outcomes from consuming a trillion ore and choose max. recursive function takes a map of available ingredients and returns max amount of fuel. each function recurses into all formulae that can be produced. memoize in case subproblems are repeated (ie, same ingredients). This approach would be too expensive. Each function would call itself up to N times (number of total formulae in input). Recursion stops when this is recursed X times such that no more formulae are possible. X is hard to define - it depends on the quantities consumed and produced in the formulas and the ore supply value (rather than size of input). memoization would not reduce complexity because subproblems overlap coincidentally rather than inherently. Something like N^X.

;; better approach is to reuse part 1. change part 1 so it takes arbitrary amount of fuel as a requirement, then call the function to create X fuel for values of X until the function returns one trillion ore (or just under).

(def ore-supply (apply * (repeat 12 10))) ; one trillion

(defn max-fuel-helper
  [quantities-formulas]
  (loop [low 0
         high ore-supply]
    (let [mid (quot (+ high low) 2)
          required-ore (ore-quantity-helper mid quantities-formulas)]
      (cond
        (< high low) mid ; binary search has been exhausted. no perfect guess.
        (= ore-supply required-ore) mid ; perfect guess
        (< ore-supply required-ore) (recur low (dec mid))
        (> ore-supply required-ore) (recur (inc mid) high)))))

(defn max-fuel
  [inputstr]
  (->> inputstr
       (parse)
       (quantity-formula-maps)
       (max-fuel-helper)))

#_
(max-fuel (util/fstr "day14_input.txt"))
;; => 6226152
