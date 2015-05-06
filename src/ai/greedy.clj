(ns ai.greedy
  (:require [clo-go.board :refer :all]))


(defn get-best-position 
  ([board color] (let [pos (atom [-1 -1])
                       max (atom 0)
                       liberty (atom 400)]
                   (do
                     (loop [x (dec board-size)]
                       (when (>= x 0)
                         (loop [y (dec board-size)]
                           (when (>= y 0)
                             (when (suicide-with-benefits? board color x y)
                               ;search for move to add points to score
                               (if (no-liberty? (put-piece board color x y) (opposite color) (dec x) y)
                                 (let [points (structure-points board (opposite color) (dec x) y)]
                                   (when (> points @max)
                                     (reset! max points)
                                     (reset! liberty 0)
                                     (reset! pos [x y]))))
                               (if (no-liberty? (put-piece board color x y) (opposite color) x (dec y))
                                 (let [points (structure-points board (opposite color) x (dec y))]
                                   (when (> points @max)
                                     (reset! max points)
                                     (reset! liberty 0)
                                     (reset! pos [x y]))))
                               (if (no-liberty? (put-piece board color x y) (opposite color) (inc x) y)
                                 (let [points (structure-points board (opposite color) (inc x) y)]
                                   (when (> points @max)
                                     (reset! max points)
                                     (reset! liberty 0)
                                     (reset! pos [x y]))))
                               (if (no-liberty? (put-piece board color x y) (opposite color) x (inc y))
                                 (let [points (structure-points board (opposite color) x (inc y))]
                                   (when (> points @max)
                                     (reset! max points)
                                     (reset! liberty 0)
                                     (reset! pos [x y]))))
                               ;search for move to that will enable to score some points as soon as posssible
                               (if (not= @liberty 0)
                                 (let [min-liberty (min
                                                     (liberty-points board (opposite color) (dec x) y)
                                                     (liberty-points board (opposite color) x (dec y))
                                                     (liberty-points board (opposite color) (inc x) y)
                                                     (liberty-points board (opposite color) x (inc y)))
                                       min-points (min
                                                    (structure-points board (opposite color) (dec x) y)
                                                    (structure-points board (opposite color) x (dec y))
                                                    (structure-points board (opposite color) (inc x) y)
                                                    (structure-points board (opposite color) x (inc y)))]
                                   (do 
                                     (when (> @liberty min-liberty)
                                       (reset! max min-points)
                                       (reset! liberty min-liberty)
                                       (reset! pos [x y]))
                                     (when (and 
                                             (= @liberty min-liberty)
                                             (< @max min-points)) 
                                       (reset! pos [x y])
                                       (reset! max min-points)))
                                     )))
                             (recur (dec y))))
                         (recur (dec x))))
                     (if (= @pos [-1 -1])
                       [(rand-int board-size) (rand-int board-size)]
                       @pos))))
  
  ([board color ko-position] (let [pos (atom [-1 -1])
                                   max (atom 0)
                                   liberty (atom 400)]
                               (do
                                 (loop [x (dec board-size)]
                                   (when (>= x 0)
                                     (loop [y (dec board-size)]
                                       (when (>= y 0)
                                         (when (suicide-with-benefits? board color x y)
                                           ;search for move to add points to score
                                           (if (no-liberty? (put-piece board color x y) (opposite color) (dec x) y)
                                             (let [points (structure-points board (opposite color) (dec x) y)]
                                               (when (> points @max)
                                                 (reset! max points)
                                                 (reset! liberty 0)
                                                 (reset! pos [x y]))))
                                           (if (no-liberty? (put-piece board color x y) (opposite color) x (dec y))
                                             (let [points (structure-points board (opposite color) x (dec y))]
                                               (when (> points @max)
                                                 (reset! max points)
                                                 (reset! liberty 0)
                                                 (reset! pos [x y]))))
                                           (if (no-liberty? (put-piece board color x y) (opposite color) (inc x) y)
                                             (let [points (structure-points board (opposite color) (inc x) y)]
                                               (when (> points @max)
                                                 (reset! max points)
                                                 (reset! liberty 0)
                                                 (reset! pos [x y]))))
                                           (if (no-liberty? (put-piece board color x y) (opposite color) x (inc y))
                                             (let [points (structure-points board (opposite color) x (inc y))]
                                               (when (> points @max)
                                                 (reset! max points)
                                                 (reset! liberty 0)
                                                 (reset! pos [x y]))))
                                           ;search for move to that will enable to score some points as soon as posssible
                                           (if (not= @liberty 0)
                                             (let [min-liberty (min
                                                                 (liberty-points board (opposite color) (dec x) y)
                                                                 (liberty-points board (opposite color) x (dec y))
                                                                 (liberty-points board (opposite color) (inc x) y)
                                                                 (liberty-points board (opposite color) x (inc y)))
                                                   min-points (min
                                                                (structure-points board (opposite color) (dec x) y)
                                                                (structure-points board (opposite color) x (dec y))
                                                                (structure-points board (opposite color) (inc x) y)
                                                                (structure-points board (opposite color) x (inc y)))]
                                               (do 
                                                 (when (> @liberty min-liberty)
                                                   (reset! max min-points)
                                                   (reset! liberty min-liberty)
                                                   (reset! pos [x y]))
                                                 (when (and 
                                                         (= @liberty min-liberty)
                                                         (< @max min-points)) 
                                                   (reset! pos [x y])
                                                   (reset! max min-points)))
                                                 )))
                                         (recur (dec y))))
                                     (recur (dec x))))
                                 (if (or 
                                       (= @pos [-1 -1])
                                       (= (add-piece board color @pos) ko-position))
                                   [(rand-int board-size) (rand-int board-size)]
                                   @pos)))))
      