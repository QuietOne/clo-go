(ns battle.battles
  (:require [battle.battle :refer :all])
  (:require [ai.monte-carlo :refer :all])
  (:require [ai.greedy :refer :all]))

(defn random-VS-random []
  (battle try-move try-move 10 10))

(defn black-random-VS-white-greedy []
  (battle try-move get-best-position 5 2))

(defn black-greedy-VS-white-random []
  (battle get-best-position try-move 2 5))

(defn greedy-VS-greedy []
  (battle get-best-position get-best-position 1 1))