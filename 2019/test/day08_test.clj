(ns day08-test
  (:require [day08 :as sut]
            [clojure.test :as t]))

#_
(sut/part1 "123456789012" 3 2)

#_
(sut/transpose [[1 2 3] [1 2 3]]) ; => [[1 1] [2 2] [3 3]]

#_
(sut/part2 "0222112222120000" 2 2)
;; => ["0 1" "1 0"]
