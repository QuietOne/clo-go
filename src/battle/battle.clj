(ns battle.battle
  (:require [clo-go.board :refer :all])
  (:require [clo-go.board-struct :refer :all]))

(defn battle [black-guess white-guess num-black-tries num-white-tries]
  (let [black-is-playing (atom true)
        white-is-playing (atom true)
        game-board (atom (board))
        ko-position (atom (board))]
    (reset-score)
    (while (and @white-is-playing @black-is-playing)
      (do
        (when @black-is-playing
          (reset! black-is-playing false)
          (loop [x num-black-tries]
            (when (> x 0)
              (let [pos (black-guess @game-board :black @ko-position)]
                (when (and
                        (not @black-is-playing)
                        (possible-move? @game-board :black pos))
                  (reset! ko-position @game-board)
                  (reset! game-board (add-piece @game-board :black pos))
                  (reset! white-is-playing true)
                  (reset! black-is-playing true)
                  (println @game-board))
                (recur (dec x))))))
        (when @white-is-playing
          (reset! white-is-playing false)
          (loop [x num-white-tries]
            (when (> x 0)
              (let [pos (white-guess @game-board :white @ko-position)]
                (when (and
                        (not @white-is-playing)
                        (possible-move? @game-board :white pos))
                  (reset! ko-position @game-board)
                  (reset! game-board (add-piece @game-board :white pos))
                  (reset! white-is-playing true)
                  (reset! black-is-playing true)
                  (println @game-board))
                (recur (dec x))))))))
    (let [scoring-board (calculate-territory @game-board)]
      (add-score-from-territory scoring-board)
      (println "Black score:" @black-score)
      (println "White score:" @white-score)
      (println scoring-board))))