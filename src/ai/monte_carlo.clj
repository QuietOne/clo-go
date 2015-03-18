(ns ai.monte-carlo
  (:require [clo-go.board :refer :all]))

(def num-of-tries 10)

(defn try-putting-piece-to []
  [(rand-int board-size) (rand-int board-size)])