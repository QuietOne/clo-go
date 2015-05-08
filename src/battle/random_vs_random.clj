(ns battle.random-vs-random
  (:require [ai.monte-carlo :refer :all])
  (:require [clo-go.board :refer :all])
  (:require [clo-go.board-struct :refer :all])
  (:require [util.persistance :refer :all]))

(def num-of-tries 10)

(defn random-VS-random []
  (let [black-is-playing (atom true)
        white-is-playing (atom true)
        game-board (atom (board))]
    (reset-score)
    (while (and @white-is-playing @black-is-playing)
      (do
        (when @black-is-playing
          (reset! black-is-playing false)
          (loop [x num-of-tries]
            (when (> x 0)
              (let [pos (try-putting-piece-to)]
                (when (and
                        (not @black-is-playing)
                        (possible-move? @game-board :black pos))
                    (reset! game-board (add-piece @game-board :black pos))
                    (reset! white-is-playing true)
                    (reset! black-is-playing true)
                    (println @game-board))
                  (recur (dec x))))))
        (when @white-is-playing
          (reset! white-is-playing false)
          (loop [x num-of-tries]
            (when (> x 0)
              (let [pos (try-putting-piece-to)]
                (when (and
                        (not @white-is-playing)
                        (possible-move? @game-board :white pos))
                    (reset! game-board (add-piece @game-board :white pos))
                    (reset! white-is-playing true)
                    (reset! black-is-playing true)
                    (println @game-board))
                (recur (dec x))))))))
    (let [scoring-board (calculate-territory @game-board)]
      (add-score-from-territory scoring-board)
      #_(persist-results @black-score @white-score @game-board "randomVSrandom")
      (println "Black score:" @black-score)
      (println "White score:" @white-score)
      (println scoring-board))))