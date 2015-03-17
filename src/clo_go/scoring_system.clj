(ns clo-go.scoring-system
  (:use [clo-go.board :only [board-size]]))

(def black-score (atom 0))
(def white-score (atom 0))

(defn color-field [board color x y]
  (cond 
    (= color :white) (assoc-in board [x y] "O")
    (= color :black) (assoc-in board [x y] "X")
    :else (assoc-in board [x y] "M")))

(defn coloring 
  ([board color x y]
    (coloring board '() color x y))
  ([board checked color x y]
    (if (and 
          (valid-pos? x y)
          (empty-field? board x y)
          (not (.contains checked [x y])))
      (->
        (color-field board color x y)
        (coloring (conj checked [x y]) color (dec x) y)
        (coloring (conj checked [x y]) color (inc x) y)
        (coloring (conj checked [x y]) color x (dec y))
        (coloring (conj checked [x y]) color x (inc y)))
      board)))

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
      (help-search board (conj checked [x y]) color (dec x) y)
      (help-search (conj checked [x y]) color x (dec y))
      (help-search (conj checked [x y]) color (inc x) y)
      (help-search (conj checked [x y]) color x (inc y)))
    board))
  
(defn ^:private help-search [board checked color x y]
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
                (= ((board x) y) "X") (swap! black-score inc)
                (= ((board x) y) "O") (swap! white-score inc))
              (recur (dec y))))
          (recur (dec x)))))
