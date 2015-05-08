(ns clo-go.board
  (:require [clo-go.board-nav :refer :all])
  (:require [clo-go.board-struct :refer :all]))

(defn ^:private not-already-checked? [coll pos]
  (not (.contains coll pos)))

#_(defn ^:private conj-if-not-already-checked [coll pos]
   (if (not-already-checked? coll pos)
     (conj coll pos)
     coll))

(defn ^:private valid-and-empty? 
  ([board pos]
    (and
      (valid-pos? pos)
      (empty-field? board pos)))
  ([board nav-f pos]
    (and
      (valid-pos? (nav-f pos))
      (empty-field? board (nav-f pos)))))

(defn ^:private valid-and-same-color? 
  ([board color pos]
    (and
      (valid-pos? pos)
      (same-color? board color pos)))
  ([board color nav-f pos]
    (and
      (valid-pos? (nav-f pos))
      (same-color? board color (nav-f pos)))))

(defn ^:private liberty-checking? 
  ([board coll color pos]
    (if (not-already-checked? coll pos)
      (or
        (liberty-checking? board coll color up pos)
        (liberty-checking? board coll color down pos)
        (liberty-checking? board coll color left pos)
        (liberty-checking? board coll color right pos))))
  ([board coll color nav-f pos]
    (or
      (valid-and-empty? board nav-f pos)
      (and
        (valid-and-same-color? board color nav-f pos)
        (liberty-checking? board (conj coll pos) color (nav-f pos))))))
    
(defn liberty? 
  ([board color pos] (liberty-checking? board '() color pos))
  ([board color x y] (liberty-checking? board '() color [x y])))

(defn no-liberty? 
  ([board color pos] 
    (and
      (valid-and-same-color? board color pos)
      (not (liberty? board color pos))))
  ([board color x y]
    (no-liberty? board color [x y])))

(defn ^:private liber-points 
  ([board coll points color pos]
    (when (and 
            (valid-pos? pos) 
            (not-already-checked? coll pos))
      (liber-points board coll points color up pos)
      (liber-points board coll points color down pos)
      (liber-points board coll points color left pos)
      (liber-points board coll points color right pos)))
  ([board coll points color nav-f pos]
    (if (valid-pos? (nav-f pos))
      (if (empty-field? board (nav-f pos))
        (if (not-already-checked? @points (nav-f pos))
          (swap! points conj (nav-f pos)))
        (if (same-color? board color (nav-f pos))
          (liber-points board (conj coll pos) points color (nav-f pos)))))))

(defn liberty-points 
  ([board color pos]
    (if (valid-and-same-color? board color pos)
      (let [points (atom '())]
        (liber-points board '() points color pos)
        (count @points))
      401))
  ([board color x y]
    (liberty-points board color [x y])))

(defn ^:private structure-points-checked 
  ([board points color pos]
    (when (and 
            (valid-pos? pos) 
            (not-already-checked? @points pos))
      (structure-points-checked board points color up pos)
      (structure-points-checked board points color down pos)
      (structure-points-checked board points color left pos)
      (structure-points-checked board points color right pos)))
  ([board points color nav-f pos]
    (when (valid-and-same-color? board color nav-f pos)
      (if (not-already-checked? @points pos) 
        (swap! points conj pos))
      (structure-points-checked board points color (nav-f pos)))))

(defn structure-points [board color pos]
  (let [points (atom '())]
    (structure-points-checked board points color pos)
    (count @points)))

(defn suicide? 
  ([board color pos]
    (and
      (suicide? board color up pos)
      (suicide? board color down pos)
      (suicide? board color left pos)
      (suicide? board color right pos)))
  ([board color nav-f pos]
    (or
      (not-valid-pos? (nav-f pos))
      (and
        (occupied? board (nav-f pos))
        (if (same-color? board color (nav-f pos))
          (no-liberty? (put-piece board color pos) color pos)
          (liberty? (put-piece board color pos) (opposite color) (nav-f pos)))))))

(defn not-suicide? [board color pos]
  (not (suicide? board color pos)))

(defn possible-move? 
  ([board color pos] 
    (and
      (valid-and-empty? board pos)
      (or
        (not-suicide? board color pos)
        (no-liberty?  (put-piece board color pos) (opposite color) (up pos))
        (no-liberty?  (put-piece board color pos) (opposite color) (down pos))
        (no-liberty?  (put-piece board color pos) (opposite color) (left pos))
        (no-liberty?  (put-piece board color pos) (opposite color) (right pos)))))
  ([board color x y] 
    (possible-move? board color [x y])))
  
;atom used for storing scores
(def black-score (atom 0))
(def white-score (atom 0))

(defn remove-struct-of-color [board color pos]
  (if (valid-and-same-color? board color pos)
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
  ([board color pos] 
    (if (possible-move? board color pos)
      (->
        (put-piece board color pos)
        (remove-if-no-liberty (opposite color) (up pos))
        (remove-if-no-liberty (opposite color) (down pos))
        (remove-if-no-liberty (opposite color) (left pos))
        (remove-if-no-liberty (opposite color) (right pos)))
      board))
  ([board color x y] 
    (add-piece board color [x y])))

;scoring system
(defn ^:private coloring-checked [board checked color pos]
  (if (and 
        (valid-and-empty? board pos)
        (not-already-checked? checked pos))
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
    (search-color board '() (atom :unknown) [x y]))
  ([board coll color pos] 
    (do
      (help-search1 board coll color pos)
      @color)))

(defn ^:private help-search1 [board coll color pos]
  (if (not-already-checked? coll pos)
    (->
      (help-search board (conj coll pos) color (up pos))
      (help-search (conj coll pos) color (down pos))
      (help-search (conj coll pos) color (left pos))
      (help-search (conj coll pos) color (right pos)))
    board))
  
(defn ^:private help-search [board checked color pos]
  (if (not= @color :no-mans-land)
    (if (valid-pos? pos)
      (if (empty-field? board pos)
        (help-search1 board checked color pos)
        (if (= @color :unknown)
          (do
            (reset! color (piece-at board pos))
            board)
          (if (not= @color (piece-at board pos))
            (do 
              (reset! color :no-mans-land)
              board)
            board)))
      board)
    board))

(defn search-and-color [board x y]
  (coloring board (search-color board x y) x y))

(defn calculate-territory [board]
  (let [scoring-board (atom board)]
    (loop [x (dec board-size)]
      (when (>= x 0)
        (loop [y (dec board-size)]
          (when (>= y 0)
            (when (empty-field? @scoring-board x y) 
              (reset! scoring-board (search-and-color @scoring-board x y)))
            (recur (dec y))))
        (recur (dec x))))
    @scoring-board))

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

(defn who-won [komi]
  (cond
    (< @black-score (+ @white-score komi)) :white
    (> @black-score (+ @white-score komi)) :black
    :else :draw))