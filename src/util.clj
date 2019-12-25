(ns util
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pp]))

(defn p
  [x]
  (pp/pprint x)
  x)

(defn fstr
  [file]
  (slurp (io/resource file)))
