(ns battle.random-vs-random
  (:require [ai.monte-carlo :refer :all])
  (:require [clo-go.board :refer :all]))

(def black-is-playing (atom true))
(def white-is-playing (atom true))

(def game-board (atom (board)))

(defn random-VS-random []
 (do
   (reset! game-board (board))
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
     (reset! white-is-playing true)
     (reset! black-is-playing true)
     (println @game-board)))

(random-VS-random)