(ns day08
  "https://adventofcode.com/2019/day/8"
  (:require [clojure.java.io :as io]))

(defn split-layers
  [size input]
  ((fn g [input]
     (if (seq input)
       (conj (g (drop size input))
             (take size input))
       '()))
   input))

(defn char-count
  [layer char]
  (->> layer
       (filter #{char})
       (count)))

(defn fewest-zero-layer
  [layers]
  (reduce (fn [acc elem]
            (if (< (char-count elem \0)
                   (char-count acc \0))
              elem
              acc))
          (first layers)
          (rest layers)))

(defn answer
  [layer]
  (* (char-count layer \1)
     (char-count layer \2)))

(defn f
  [input width height]
  (->> input
       (remove #{\newline})
       (split-layers (* width height))
       (fewest-zero-layer)
       (answer)))

#_
(f "123456789012" 3 2)

#_
(f (slurp (io/resource "day08_input.txt")) 25 6)
;; => 2318
;; correct

;;;;;;;;;;;;
;; PART 2
;; first layer in front. precedence: black 0, white 1, transparent 2
;;;;;;;;;;;;

(defn transpose
  [m]
  (apply mapv vector m))

#_
(transpose [[1 2 3] [1 2 3]]) ; => [[1 1] [2 2] [3 3]]

(defn decode-position
  "earlier positions have priority. remove all leading 2s. "
  [chars]
  (->> chars
       (drop-while #{\2})
       (first)))

(defn shape
  [width coll]
  ((fn g [coll]
     (if (seq coll)
       (conj (g (drop width coll))
             (take width coll))
       '()))
   coll))

(defn f2
  [input width height]
  (->> input
       (remove #{\newline})
       (split-layers (* width height))
       (transpose)
       (map decode-position)
       (shape width)
       (mapv (partial interpose \ ))
       (mapv (partial apply str))))

#_
(f2 "0222112222120000" 2 2)
#_
["01" "10"]

#_
(f2 (slurp (io/resource "day08_input.txt")) 25 6)
#_
["0 1 1 0 0 1 0 0 1 0 1 1 1 1 0 0 1 1 0 0 1 1 1 0 0"
 "1 0 0 1 0 1 0 0 1 0 1 0 0 0 0 1 0 0 1 0 1 0 0 1 0"
 "1 0 0 1 0 1 1 1 1 0 1 1 1 0 0 1 0 0 0 0 1 1 1 0 0"
 "1 1 1 1 0 1 0 0 1 0 1 0 0 0 0 1 0 0 0 0 1 0 0 1 0"
 "1 0 0 1 0 1 0 0 1 0 1 0 0 0 0 1 0 0 1 0 1 0 0 1 0"
 "1 0 0 1 0 1 0 0 1 0 1 0 0 0 0 0 1 1 0 0 1 1 1 0 0"]

;; => AHFCB
