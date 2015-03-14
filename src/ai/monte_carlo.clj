(ns ai.monte-carlo
  (:require [clo-go.board :refer :all]))

(def num-of-tries 10)

(defn try-putting-piece-to []
  [(rand-int (inc board-size)) (rand-int (inc board-size))])