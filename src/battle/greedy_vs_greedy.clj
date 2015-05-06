(ns battle.greedy-vs-greedy
  (:require [ai.greedy :refer :all])
  (:require [clo-go.board :refer :all])
  (:require [util.persistance :refer :all]))

(def num-of-tries-white 10)
(def num-of-tries-black 10)

(defn greedy-VS-greedy []
  (let [black-is-playing (atom true)
        white-is-playing (atom true)
        game-board (atom (board))
        ko-position (atom (board))]
        (do
          (reset-score)
          (while (and @white-is-playing @black-is-playing)
            (do
              (if @black-is-playing
                (do
                  (reset! black-is-playing false)
                  (loop [x num-of-tries-black]
                    (when (> x 0)
                      (let [pos (get-best-position @game-board :black @ko-position)]
                        (do
                          (if (and
                                (not @black-is-playing)
                                (suicide-with-benefits? @game-board :black pos))
                            (do
                              (reset! ko-position @game-board)
                              (reset! game-board (add-piece @game-board :black pos))
                              (reset! white-is-playing true)
                              (reset! black-is-playing true)
                              (println @game-board)))
                          (recur (dec x))))))))
              (if @white-is-playing
                (do
                  (reset! white-is-playing false)
                  (loop [x num-of-tries-white]
                    (when (> x 0)
                      (let [pos (get-best-position @game-board :white @ko-position)]
                        (if (and
                              (not @white-is-playing)
                              (suicide-with-benefits? @game-board :white pos))
                          (do
                            (reset! ko-position @game-board)
                            (reset! game-board (add-piece @game-board :white pos))
                            (reset! white-is-playing true)
                            (reset! black-is-playing true)
                            (println @game-board)))
                        (recur (dec x)))))))))
          (add-score-from-territory (calculate-territory @game-board))
          ;(persist-results @black-score @white-score @game-board "greedy-VS-greedy")
          (println "Black score:" @black-score)
          (println "White score:" @white-score)
          (println @game-board))))

#_(loop [x 1000]
   (when (> x 0)
     (println x)
     (greedy-VS-greedy)
     (recur (dec x))))

(greedy-VS-greedy)