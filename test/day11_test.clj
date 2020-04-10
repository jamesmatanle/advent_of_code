(ns day11-test
  (:require [day11 :as sut]
            [clojure.test :as t]))

(sut/next-direction 0 0)
(sut/next-direction 0 1)
(sut/next-direction 1 0)
(sut/next-direction 1 1)
(sut/next-direction 2 0)
(sut/next-direction 2 1)
(sut/next-direction 3 0)
(sut/next-direction 3 1)
