(ns day06-test
  (:require [day06 :as sut]
            [clojure.test :as t]))


(def small-input "COM)B
B)C
C)D
D)E
E)F
B)G
G)H
D)I
E)J
J)K
K)L")

#_
(sut/parse-to-map small-input)

#_
(sut/part1 small-input)

(def small-input-2 "COM)B
B)C
C)D
D)E
E)F
B)G
G)H
D)I
E)J
J)K
K)L
K)YOU
I)SAN")

#_
(sut/part2 small-input-2)
