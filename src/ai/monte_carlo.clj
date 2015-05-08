(ns ai.monte-carlo
  (:require [clo-go.board-struct :refer :all]))


(defn try-putting-piece-to []
  [(rand-int board-size) (rand-int board-size)])