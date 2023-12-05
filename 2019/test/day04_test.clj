(ns day04-test
  (:require [day04 :as sut]
            [clojure.test :as t]))

#_
(mapv sut/has-2-same-adj? [[1 2 2 4] [2 2] [1 2 3] [3 2 1] [1] [0 0]])

#_
(mapv sut/digits-never-decrease? [[1 2 5] [1 2 2 4 6 8 9] [1 2 2 0] [1 0 2 2] [0] [1 2]])

#_
(mapv sut/parse-to-sequence [1 125 1225 0 12250])
;; => [[1] [1 2 5] [1 2 2 5] [0] [1 2 2 5 0]]

#_
(mapv sut/has-2-same-adj?-2 [[1 2 2 3] [1 2 2 2 3] [1 2 2 2 2 3]])
;; => [true false false]
