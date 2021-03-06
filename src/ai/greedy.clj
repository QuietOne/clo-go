(ns ai.greedy
  (:require [clo-go.board :refer :all])
  (:require [clo-go.board-struct :refer :all])
  (:require [clo-go.board-nav :refer :all]))

(defn ^:private check-for-immediate-gain [board color max liberty pos nav-f x y]
  (if (no-liberty? (put-piece board color x y) (opposite color) (nav-f x y))
    (let [points (structure-points board (opposite color) (nav-f x y))]
      (when (> points @max)
        (reset! max points)
        (reset! liberty 0)
        (reset! pos [x y])))))

(defn ^:private min-f-in-all-directs [f board color pos]
  (min
    (f board (opposite color) (up pos))
    (f board (opposite color) (down pos))
    (f board (opposite color) (left pos))
    (f board (opposite color) (right pos))))

(defn ^:private min-liberty-points [board color pos]
  (min-f-in-all-directs liberty-points board color pos))

(defn ^:private min-points-gain [board color pos]
  (min-f-in-all-directs structure-points board color pos))

(defn ^:private random-move []
  [(rand-int board-size) (rand-int board-size)])
  
(defn ^:private search-immediate-gain [board color max liberty pos x y]
  (do
    (check-for-immediate-gain board color max liberty pos up x y)
    (check-for-immediate-gain board color max liberty pos down x y)
    (check-for-immediate-gain board color max liberty pos left x y)
    (check-for-immediate-gain board color max liberty pos right x y)))

(defn ^:private search-weakest-structure [board color max liberty pos x y]
  (if (not= @liberty 0)
    (let [min-liberty (min-liberty-points board color [x y])
          min-points (min-points-gain board color [x y])]
      (when (> @liberty min-liberty)
        (reset! max min-points)
        (reset! liberty min-liberty)
        (reset! pos [x y]))
      (when (and 
              (= @liberty min-liberty)
              (< @max min-points)) 
        (reset! pos [x y])
        (reset! max min-points)))))

(defn ^:private set-pos-valid [pos color board ko-position]
  (if (or 
        (= @pos [-1 -1])
        (= (add-piece board color @pos) ko-position))
    (random-move)
    @pos))
  
(defn move [board color ko-position] 
  (let [pos (atom [-1 -1])
        max (atom 0)
        liberty (atom 400)]
    (loop [x (dec board-size)]
      (when (>= x 0)
        (loop [y (dec board-size)]
          (when (>= y 0)
            (when (possible-move? board color x y)
              (search-immediate-gain board color max liberty pos x y)
              (search-weakest-structure board color max liberty pos x y))
            (recur (dec y))))
        (recur (dec x))))
    (set-pos-valid pos color board ko-position)))