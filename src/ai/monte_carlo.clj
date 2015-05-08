(ns ai.monte-carlo
  (:require [clo-go.board-struct :refer :all]))

(defn try-move [board color ko-position]
  [(rand-int board-size) (rand-int board-size)])