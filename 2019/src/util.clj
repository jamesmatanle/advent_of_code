(ns util
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.string :as string]))

(defn p
  [x]
  (pp/pprint x)
  x)

(defn fstr
  [file]
  (-> file (io/resource) (slurp) (string/trim-newline)))
