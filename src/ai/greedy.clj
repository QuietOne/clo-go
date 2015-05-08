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
  
(defn get-best-position 
  ([board color] 
    (let [pos (atom [-1 -1])
          max (atom 0)
          liberty (atom 400)]
      (loop [x (dec board-size)]
        (when (>= x 0)
          (loop [y (dec board-size)]
            (when (>= y 0)
              (when (possible-move? board color x y)
                ;search for move to add points for immediate gain
                (check-for-immediate-gain board color max liberty pos up x y)
                (check-for-immediate-gain board color max liberty pos down x y)
                (check-for-immediate-gain board color max liberty pos left x y)
                (check-for-immediate-gain board color max liberty pos right x y)
                ;search for move to that will enable to score some points as soon as posssible
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
              (recur (dec y))))
          (recur (dec x))))
        (if (= @pos [-1 -1]) 
          (random-move)
          @pos)))
  
  ([board color ko-position] 
    (let [pos (atom [-1 -1])
          max (atom 0)
          liberty (atom 400)]
      (do
        (loop [x (dec board-size)]
          (when (>= x 0)
            (loop [y (dec board-size)]
              (when (>= y 0)
                (when (possible-move? board color x y)
                  ;search for move to add points to score
                  (check-for-immediate-gain board color max liberty pos up x y)
                  (check-for-immediate-gain board color max liberty pos down x y)
                  (check-for-immediate-gain board color max liberty pos left x y)
                  (check-for-immediate-gain board color max liberty pos right x y)
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
      