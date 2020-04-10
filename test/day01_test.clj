(ns day01-test
  (:require [day01 :as sut]
            [clojure.test :as t]))

(mapv sut/part1
      [12 14 1969 100756]) ; => [2 2 654 33583]

(sut/part2 14) ; => 2
(sut/part2 1969) ; => 966
(sut/part2 100756) ; => 50346
