(ns battle.random-vs-random
  (:require [ai.monte-carlo :refer :all])
  (:require [clo-go.board :refer :all])
  (:require [util.persistance :refer :all]))

(defn random-VS-random []
  (let [black-is-playing (atom true)
        white-is-playing (atom true)
        game-board (atom (board))]
        (do
          (reset-score)
          (while (and @white-is-playing @black-is-playing)
            (do
              (if @black-is-playing
                (do
                  (reset! black-is-playing false)
                  (loop [x num-of-tries]
                    (when (> x 0)
                      (let [pos (try-putting-piece-to)]
                        (do
                          (if (and
                                (not @black-is-playing)
                                (suicide-with-benefits? @game-board :black pos))
                            (do
                              (reset! game-board (add-piece @game-board :black pos))
                              (reset! white-is-playing true)
                              (reset! black-is-playing true)))
                          (recur (dec x))))))))
              (if @white-is-playing
                (do
                  (reset! white-is-playing false)
                  (loop [x num-of-tries]
                    (when (> x 0)
                      (let [pos (try-putting-piece-to)]
                        (if (and
                              (not @white-is-playing)
                              (suicide-with-benefits? @game-board :white pos))
                          (do
                            (reset! game-board (add-piece @game-board :white pos))
                            (reset! white-is-playing true)
                            (reset! black-is-playing true)))
                        (recur (dec x)))))))))
          (add-score-from-territory (calculate-territory @game-board))
          (persist-results @black-score @white-score @game-board "randomVSrandom")
          #_(println "Black score:" @black-score)
          #_(println "White score:" @white-score)
          #_(println @game-board))))

(loop [x 10000]
  (when (> x 0)
    (println x)
    (random-VS-random)
    (recur (dec x))))