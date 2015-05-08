(ns battle.battle
  (:require [clo-go.board :refer :all])
  (:require [clo-go.board-struct :refer :all]))

(defn ^:private move [is-playing guess color num-of-tries game-board ko-position other-is-playing]
  (when @is-playing
    (reset! is-playing false)
    (loop [x num-of-tries]
      (when (> x 0)
        (let [pos (guess @game-board color @ko-position)]
          (when (and
                  (not @is-playing)
                  (possible-move? @game-board color pos))
            (reset! ko-position @game-board)
            (swap! game-board add-piece color pos)
            (reset! is-playing true)
            (reset! other-is-playing true)
            (println @game-board))
          (recur (dec x)))))))

(defn battle [black-guess white-guess black-n-tries white-n-tries]
  (let [black-is-playing (atom true)
        white-is-playing (atom true)
        game-board (atom (board))
        ko-position (atom (board))]
    (reset-score)
    (while (and @white-is-playing @black-is-playing)
      (do
        (move black-is-playing 
              black-guess
              :black
              black-n-tries
              game-board
              ko-position
              white-is-playing)
        (move white-is-playing
              white-guess
              :white
              white-n-tries
              game-board
              ko-position
              black-is-playing)))
    (let [scoring-board (calculate-territory @game-board)]
      (add-score-from-territory scoring-board)
      (println "Black score:" @black-score)
      (println "White score:" @white-score)
      (println scoring-board))))