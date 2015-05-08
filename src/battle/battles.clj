(ns battle.battles
  (:require [battle.battle :refer :all])
  (:require [ai.monte-carlo :as random])
  (:require [ai.greedy :as greedy]))

(defn random-VS-random []
  (battle random/move random/move 10 10))

(defn black-random-VS-white-greedy []
  (battle random/move greedy/move 5 2))

(defn greedy-VS-greedy []
  (battle greedy/move greedy/move 1 1))