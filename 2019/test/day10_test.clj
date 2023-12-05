(ns day10-test
  (:require [day10 :as sut]
            [clojure.test :as t]))



(sut/gcf 3 2)
(sut/gcf 6 4)
(sut/gcf 9 6)
(sut/gcf 12 8)

(sut/slope [0 0] [12 8])
(sut/slope [0 0] [9 8])
(sut/slope [0 0] [0 2])
(sut/slope [0 0] [0 1])
(sut/slope [0 0] [1 0])
(sut/slope [0 0] [2 0])

#_
(sut/part1
 "
.#..#
.....
#####
....#
...##") ; => [[3 4] 8]

#_
(sut/part1
 "
......#.#.
#..#.#....
..#######.
.#.#.###..
.#..#.....
..#....#.#
#..#....#.
.##.#..###
##...#..#.
.#....####") ; => [[5 8] 33]

#_
(sut/part1
 "
#.#...#.#.
.###....#.
.#....#...
##.#.#.#.#
....#.#.#.
.##..###.#
..#...##..
..##....##
......#...
.####.###.") ; => [[1 2] 35]

#_
(sut/part1
 "
.#..#..###
####.###.#
....###.#.
..###.##.#
##.##.#.#.
....###..#
..#.#..#.#
#..#.#.###
.##...##.#
.....#.#..") ; => [[6 3] 41]

(def large-input
  "
.#..##.###...#######
##.############..##.
.#.######.########.#
.###.#######.####.#.
#####.##.#.##.###.##
..#####..#.#########
####################
#.####....###.#.#.##
##.#################
#####.##.###..####..
..######..##.#######
####.##.####...##..#
.#####..#.######.###
##...#.##########...
#.##########.#######
.####.#.###.###.#.##
....##.##.###..#####
.#.#.###########.###
#.#.#.#####.####.###
###.##.####.##.#..##")

#_
(sut/part1 large-input) ; => [[11 13] 210]



#_
(sut/distance [0 0] [1 1])

(sut/degrees [1 0])
(sut/degrees [0 1])
(sut/degrees [0 -1])
(sut/degrees [-1 -1])
(sut/degrees [1 1])

#_
(sut/angle [0 0] [0 1])

#_
(sut/list-circles
 {0 [[1 [0 1]] [2 [0 2]]]
  90 [[1 [1 0]]]})
;; => ([0 1] [1 0] [0 2])


#_
(sut/part2 "
.#....#####...#..
##...##.#####..##
##...#...#.#####.
..#.....#...###..
..#.#.....#....##" [8 3])
;; => ([8 1] [9 0] [9 1] [10 0] [9 2] [11 1] [12 1] [11 2] [15 1] [12 2] [13 2] [14 2] [15 2] [12 3] [16 4] [15 4] [10 4] [4 4] [2 4] [2 3] [0 2] [1 2] [0 1] [1 1] [5 2] [1 0] [5 1] [6 1] [6 0] [7 0] [8 0] [10 1] [14 0] [16 1] [13 3] [14 3])

#_
(let [res (sut/part2 large-input [11 13])]
  [(nth res 0)
   (nth res 1)
   (nth res 2)
   (nth res 9)
   (nth res 19)
   (nth res 49)
   (nth res 99)
   (nth res 198)
   (nth res 199)
   (nth res 200)
   (nth res 298)])
;; => [[11 12] [12 1] [12 2] [12 8] [16 0] [16 9] [10 16] [9 6] [8 2] [10 9] [11 1]]

;; The 1st asteroid to be vaporized is at 11,12.
;; The 2nd asteroid to be vaporized is at 12,1.
;; The 3rd asteroid to be vaporized is at 12,2.
;; The 10th asteroid to be vaporized is at 12,8.
;; The 20th asteroid to be vaporized is at 16,0.
;; The 50th asteroid to be vaporized is at 16,9.
;; The 100th asteroid to be vaporized is at 10,16.
;; The 199th asteroid to be vaporized is at 9,6.
;; The 200th asteroid to be vaporized is at 8,2.
;; The 201st asteroid to be vaporized is at 10,9.
;; The 299th and final asteroid to be vaporized is at 11,1.
