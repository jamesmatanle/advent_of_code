(ns day04)



;; 6 digit number
;; within input range
;; 2 adjacent digits are the same
;; the digits never decrease (L->R)

(def input
  [245318 765747])

(defn has-2-same-adj?
  "returns first 2 adjacent digits that are the same, else empty."
  [coll]
  (true?
   (reduce (fn [acc elem]
             (if (= acc elem)
               (reduced true)
               elem))
           (first coll)
           (rest coll))))

#_
(mapv has-2-same-adj? [[1 2 2 4] [2 2] [1 2 3] [3 2 1] [1] [0 0]])

(defn digits-never-decrease?
  ([digits]
   (digits-never-decrease? (first digits) (rest digits)))
  ([x digits]
   (cond
     (empty? digits) true
     (<= x (first digits)) (digits-never-decrease? (first digits)
                                                   (rest digits))
     :else false)))

#_
(mapv digits-never-decrease? [[1 2 5] [1 2 2 4 6 8 9] [1 2 2 0] [1 0 2 2] [0] [1 2]])

(defn parse-to-sequence
  [number]
  (if (< 0 (int (/ number 10)))
    (conj (parse-to-sequence (int (/ number 10)))
          (mod number 10))
    [(mod number 10)]))

#_
(mapv parse-to-sequence [1 125 1225 0 12250])

(defn satisfies?
  [elem]
  ((every-pred digits-never-decrease?
               has-2-same-adj?)
   (parse-to-sequence elem)))

(defn test
  [input]
  (->> (apply range input)
       (reduce (fn [acc elem]
                 (if (satisfies? elem)
                   (+ acc 1)
                   acc))
               0)))

#_
(test input)

;; => 1079
;; correct

;;;;;;;;;
;; PART 2

(defn adjacent-groups
  [coll]
  (reduce (fn [acc elem]
            (if (= (first (last acc)) elem)
              (conj (vec (butlast acc))
                    (conj (last acc)
                          elem))
              (conj acc [elem])))
          []
          coll))

(defn has-2-same-adj?
  "returns first 2 adjacent digits that are the same, else empty."
  [coll]
  (->> coll
       (adjacent-groups)
       (filter (fn [x]
                 (= 2 (count x))))
       (seq)
       (boolean)))

#_
(mapv has-2-same-adj? [[1 2 2 3] [1 2 2 2 3] [1 2 2 2 2 3]])

#_
(test input)

;; => 699
;; correct
