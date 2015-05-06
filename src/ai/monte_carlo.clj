(ns ai.monte-carlo
  (:require [clo-go.board :refer :all]))


(defn try-putting-piece-to []
  [(rand-int board-size) (rand-int board-size)])