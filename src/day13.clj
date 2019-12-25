(ns day13
  (:require [day09]
            [clojure.core.async :as async]
            [clojure.edn :as edn]))

;;;;;;;;;;;;
;; PART 1
;;;;;;;;;;;;

(defn f
  [inputstr]
  (->> (day09/f inputstr [])
       (partition-all 3)
       (map #(nth % 2))
       (filter (partial = 2))
       (count)))

#_
(f (util/fstr "day13_input.txt"))
;; => 420

;;;;;;;;;;;;
;; PART 2
;; Send function as input to the game so that game process will read output so far, print game, and prompt user for input.
;; GAME INPUT: 0: stay. -1: left. 1: right
;;;;;;;;;;;;

(defn add-quarters
  [inputstr]
  (->> inputstr (rest) (concat [\2]) (apply str)))

(defn run-game!
  [memorystr in out]
  (-> memorystr
      (add-quarters)
      (day09/execute-string in out))
  nil)

(defn mk-empty-board
  "assumes no negative values for x and y."
  [board-data]
  (->> 0
       (repeat (->> board-data (map (comp first first)) (apply max) (inc)))
       (repeat (->> board-data (map (comp second first)) (apply max) (inc)))
       (mapv vec)))

#_
(defn print-score!
  [game-state]
  (println
   (->> game-state (filter (comp (partial = [-1 0]) first)) (last) (last))))

#_
(defn print-game!
  [game-state]
  (let [board-data (remove (comp (partial = [-1 0]) first) game-state)]
    (println
     (apply str (->> board-data
                     (reduce (fn [board [[x y] v]]
                               (assoc-in board [y x] v))
                             (mk-empty-board board-data))
                     (mapv (partial apply str "\n" )))))))

(defn read-output-update-state!
  [game-output game-state]
  (loop [outputs []]
    (if (= 3 (count outputs))
      (do (swap! game-state
                 assoc
                 (vec (take 2 outputs))
                 (last outputs))
          (recur []))
      (let [[val ch] (async/alts!! [game-output] :default :complete)]
        (if (= :complete val)
          nil
          (recur (conj outputs val)))))))

(defn optimal-input
  "return input command to move the paddle under the ball."
  [game-state]
  (let [paddle-x (ffirst (first (filter (comp (partial = 3) second) game-state)))
        ball-x (ffirst (first (filter (comp (partial = 4) second) game-state)))]
    (cond
      (< paddle-x ball-x) 1
      (> paddle-x ball-x) -1
      :else 0)))

(defn game-input-output-loop!
  [game-input game-output]
  (let [game-state (atom {})]
    (loop []
      (let [[v ch]
            (async/alts!! [(async/timeout 1000)
                           [game-input (fn []
                                         (read-output-update-state! game-output
                                                                    game-state)
                                         #_(print-score! @game-state)
                                         #_(print-game! @game-state)
                                         (optimal-input @game-state)
                                         #_(edn/read-string (read-line)))]])]
        (if (= ch game-input)
          (recur)
          nil)))))

(defn f
  []
  (let [game-input (async/chan)
        game-output (async/chan 100000)]
    (future
      (try
        (run-game! (util/fstr "day13_input.txt")
                   game-input
                   game-output)
        (catch Exception e
          (println e))))
    (game-input-output-loop! game-input game-output)
    (last (async/<!! (async/reduce conj [] game-output)))))

#_
(f)
;; => 21651
