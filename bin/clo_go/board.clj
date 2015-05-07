(ns clo-go.board
  (:require [clo-go.board-nav :refer :all])
  (:require [clo-go.board-struct :refer :all]))

(defn ^:private liber? [board v color pos]
  (if (not (.contains v pos))
    (or
      (or
        (and 
          (valid-pos? (up pos))
          (empty-field? board (up pos)))
        (and
          (valid-pos? (up pos))
          (same-color? board color (up pos))
          (liber? board (conj v pos) color (up pos))))
      (or
        (and
          (valid-pos? (down pos))
          (empty-field? board (down pos)))
        (and 
          (valid-pos? (down pos))
          (same-color? board color (down pos))
          (liber? board (conj v pos) color (down pos))))
      (or
        (and
          (valid-pos? (left pos))
          (empty-field? board (left pos)))
        (and           
          (valid-pos? (left pos))
          (same-color? board color (left pos))
          (liber? board (conj v pos) color (left pos))))
      (or
        (and
          (valid-pos? (right pos))
          (empty-field? board (right pos)))
        (and 
          (valid-pos? (right pos))
          (same-color? board color (right pos))
          (liber? board (conj v pos) color (right pos)))))))
        

(defn liberty? 
  ([board color x y] (liber? board '() color [x y]))
  ([board color pos] (liber? board '() color pos)))

(defn no-liberty? 
  ([board color x y]
    (and
      (valid-pos? x y)
      (same-color? board color x y)
      (not (liberty? board color x y))))
  ([board color [x y]] 
    (no-liberty? board color x y)))

(defn ^:private liber-points [board v points color pos]
  (if (and (valid-pos? pos) (not (.contains v pos)))
    (do
      (if (valid-pos? (up pos))
        (if (empty-field? board (up pos))
          (if (not (.contains @points (up pos))) 
            (swap! points conj (up pos)))
          (if (same-color? board color (up pos))
            (liber-points board (conj v pos) points color (up pos)))))
      (if (valid-pos? (down pos))
        (if (empty-field? board (down pos))
          (if (not (.contains @points (down pos)))
            (swap! points conj (down pos)))
          (if (same-color? board color (down pos))
            (liber-points board (conj v pos) points color (down pos)))))
      (if (valid-pos? (left pos))
        (if (empty-field? board (left pos))
          (if (not (.contains @points (left pos)))
            (swap! points conj (left pos)))
          (if (same-color? board color (left pos))
            (liber-points board (conj v pos) points color (left pos)))))
      (if (valid-pos? (right pos))
        (if (empty-field? board (right pos))
          (if (not (.contains @points (right pos)))
            (swap! points conj (right pos)))
          (if (same-color? board color (right pos))
            (liber-points board (conj v pos) points color (right pos))))))))

(defn liberty-points 
  ([board color pos]
    (if (valid-pos? pos)
      (if (same-color? board color pos)
        (let [points (atom '())]
          (liber-points board '() points color pos)
          (count @points))
        401)
      401))
  ([board color x y]
    (liberty-points board color [x y])))

(defn ^:private str-points [board v color [x y]]
  (if (and (valid-pos? x y) (not (.contains @v [x y])))
    (do
      (if (and
            (valid-pos? (up x y))
            (same-color? board color (up x y)))
        (do
          (if (not (.contains @v [x y])) (swap! v conj [x y]))
          (str-points board v color (up x y))))
      (if (and
            (valid-pos? (down x y))
            (same-color? board color (down x y)))
        (do
          (if (not (.contains @v [x y])) (swap! v conj [x y]))
          (str-points board v color (down x y))))
      (if (and
            (valid-pos? (left x y))
            (same-color? board color (left x y)))
        (do
          (if (not (.contains @v [x y])) (swap! v conj [x y]))
          (str-points board v color (left x y))))
      (if (and
            (valid-pos?  (right x y))
            (same-color? board color (right x y)))
        (do
          (if (not (.contains @v [x y])) (swap! v conj [x y]))
          (str-points board v color (right x y)))))))

(defn structure-points [board color x y]
  (let [points (atom '())]
    (str-points board points color [x y])
    (count @points)))

(defn suicide? [board color x y]
  (and
    (or
      (not-valid-pos? (up x y))
      (and
        (occupied? board (up x y))
        (if (same-color? board color (up x y))
          (no-liberty? (put-piece board color x y) color x y)
          (liberty? (put-piece board color x y) (opposite color) (up x y)))))
    (or
      (not-valid-pos? (down x y))
      (and
        (occupied? board (down x y))
        (if (same-color? board color (down x y))
          (no-liberty? (put-piece board color x y) color x y)
          (liberty? (put-piece board color x y) (opposite color) (down x y)))))
    (or
      (not-valid-pos? (left x y))
      (and
        (occupied? board (left x y))
        (if (same-color? board color (left x y))
          (no-liberty? (put-piece board color x y) color x y)
          (liberty? (put-piece board color x y) (opposite color) (left x y)))))
    (or
      (not-valid-pos? (right x y))
      (and
        (occupied? board (right x y))
        (if (same-color? board color (right x y))
          (no-liberty? (put-piece board color x y) color x y)
          (liberty? (put-piece board color x y) (opposite color) (right x y)))))))

(defn not-suicide? [board color x y]
  (not (suicide? board color x y)))

(defn suicide-with-benefits? 
  ([board color x y] 
    (and
      (valid-pos? x y)
      (empty-field? board x y)
      (or
        (not-suicide? board color x y)
        (or
          (no-liberty?  (put-piece board color x y) (opposite color) (up x y))
          (no-liberty?  (put-piece board color x y) (opposite color) (down x y))
          (no-liberty?  (put-piece board color x y) (opposite color) (left x y))
          (no-liberty?  (put-piece board color x y) (opposite color) (right x y))))))
  ([board color [x y]] 
    (suicide-with-benefits? board color x y)))
  
;atom used for storing scores
(def black-score (atom 0))
(def white-score (atom 0))

(defn remove-struct-of-color [board color pos]
  (if (and
        (valid-pos? pos)
        (same-color? board color pos))
    (do
      (if (= (opposite color) :white)
        (swap! white-score inc)
        (swap! black-score inc))
      (->
        (remove-piece board pos)
        (remove-struct-of-color color (up pos))
        (remove-struct-of-color color (down pos))
        (remove-struct-of-color color (left pos))
        (remove-struct-of-color color (right pos))))
    board))

(defn remove-if-no-liberty [board color pos]
  (if (and 
        (valid-pos? pos)
        (occupied? board pos)
        (no-liberty? board color pos))
    (remove-struct-of-color board color pos)
    board))

(defn add-piece 
  ([board color x y] 
    (if (suicide-with-benefits? board color x y)
      (->
        (put-piece board color x y)
        (remove-if-no-liberty (opposite color) (up x y))
        (remove-if-no-liberty (opposite color) (down x y))
        (remove-if-no-liberty (opposite color) (left x y))
        (remove-if-no-liberty (opposite color) (right x y)))
      board))
  ([board color [x y]] 
    (add-piece board color x y)))

;scoring system
(defn ^:private coloring-checked [board checked color pos]
  (if (and 
        (valid-pos? pos)
        (empty-field? board pos)
        (not (.contains checked pos)))
    (->
      (color-field board color pos)
      (coloring-checked (conj checked pos) color (up pos))
      (coloring-checked (conj checked pos) color (down pos))
      (coloring-checked (conj checked pos) color (left pos))
      (coloring-checked (conj checked pos) color (right pos)))
    board))

(defn coloring [board color x y]
  (coloring-checked board '() color [x y]))

(declare ^:private help-search)
(declare ^:private help-search1)

(defn search-color 
  ([board x y] 
    (search-color board '() (atom :unknown) x y))
  ([board checked color x y] 
    (do
      (help-search1 board checked color x y)
      @color)))

(defn ^:private help-search1 [board checked color x y]
  (if (not (.contains checked [x y]))
    (->
      (help-search board (conj checked [x y]) color (up x y))
      (help-search (conj checked [x y]) color (down x y))
      (help-search (conj checked [x y]) color (left x y))
      (help-search (conj checked [x y]) color (right x y)))
    board))
  
(defn ^:private help-search 
  ([board checked color x y]
    (if (not= @color :no-mans-land)
      (if (valid-pos? x y)
        (if (empty-field? board x y)
          (help-search1 board checked color x y)
          (if (= @color :unknown)
            (do
              (reset! color (piece-at board x y))
              board)
            (if (not= @color (piece-at board x y))
              (do 
                (reset! color :no-mans-land)
                board)
              board)))
        board)
      board))
  ([board checked color [x y]] (help-search board checked color x y)))

(defn search-and-color [board x y]
  (coloring board (search-color board x y) x y))

(defn calculate-territory [board]
  (let [scoring-board (atom board)]
    (do
      (loop [x (dec board-size)]
        (when (>= x 0)
          (loop [y (dec board-size)]
            (when (>= y 0)
              (when (empty-field? @scoring-board x y) 
                (reset! scoring-board (search-and-color @scoring-board x y)))
              (recur (dec y))))
          (recur (dec x))))
      @scoring-board)))

(defn reset-score []
  (reset! white-score 0)
  (reset! black-score 0))

(defn add-score-from-territory [scoring-board]
  (loop [x (dec board-size)]
    (when (>= x 0)
      (loop [y (dec board-size)]
        (when (>= y 0)
          (cond 
            (= (territory-color scoring-board x y) :black) (swap! black-score inc)
            (= (territory-color scoring-board x y) :white) (swap! white-score inc))
          (recur (dec y))))
      (recur (dec x)))))