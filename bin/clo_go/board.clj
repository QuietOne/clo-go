(ns clo-go.board)

(def board-size 19)

(defn board []
  (vec (repeat board-size (vec (repeat board-size '-)))))

(defn put-piece [board color x y]
  (if (= color :white)
    (assoc-in board [x y] 'o)
    (assoc-in board [x y] 'x)))

(defn piece-at [board x y]
  (cond 
    (= ((board x) y) 'x) :black
    (= ((board x) y) 'o) :white
    :else :empty))

(defn same-color?
  ([board color x y]
    (= color (piece-at board x y)))
  ([board x y x1 y1]
    (= (piece-at board x y) (piece-at board x1 y1))))

(defn not-same-color? [board x y x1 y1]
  (not (same-color? board x y x1 y1)))

(defn opposite [color]
  (cond
    (= color :white) :black
    (= color :black) :white
    :else :empty))

(defn empty-field? [board x y]
  (= (piece-at board x y) :empty))

(defn valid-pos? [x y]
  (and
    (< x board-size)
    (< y board-size)
    (>= x 0)
    (>= y 0)))

(defn not-valid-pos? [x y]
  (not (valid-pos? x y)))

(defn occupied? [board x y]
  (or
    (not-valid-pos? x y)
    (not (empty-field? board x y))))

(defn ^:private remove-piece [board x y]
  (assoc-in board [x y] '-))

(defn ^:private liber? [board v color x y]
  (if (not (.contains v [x y]))
    (or
      (or
        (and 
          (valid-pos? (dec x) y)
          (empty-field? board (dec x) y))
        (and
          (valid-pos? (dec x) y)
          (same-color? board color (dec x) y)
          (liber? board (conj v [x y]) color (dec x) y)))
      (or
        (and
          (valid-pos? (inc x) y)
          (empty-field? board (inc x) y))
        (and 
          (valid-pos? (inc x) y)
          (same-color? board color (inc x) y)
          (liber? board (conj v [x y]) color (inc x) y)))
      (or
        (and
          (valid-pos? x (dec y))
          (empty-field? board x (dec y)))
        (and           
          (valid-pos? x (dec y))
          (same-color? board color x (dec y))
          (liber? board (conj v [x y]) color x (dec y))))
      (or
        (and
          (valid-pos? x (inc y))
          (empty-field? board x (inc y)))
        (and 
          (valid-pos? x (inc y))
          (same-color? board color x (inc y))
          (liber? board (conj v [x y]) color x (inc y)))))))
        

(defn liberty? [board color x y]
  (liber? board '() color x y))

(defn no-liberty? [board color x y]
  (and
    (valid-pos? x y)
    (same-color? board color x y)
    (not (liberty? board color x y))))

(defn ^:private liber-points [board v points color x y]
  (if (and (valid-pos? x y) (not (.contains v [x y])))
    (do
      (if (valid-pos? (dec x) y)
        (if (empty-field? board (dec x) y)
          (if (not (.contains @points [(dec x) y])) 
            (swap! points conj [(dec x) y]))
          (if (same-color? board color (dec x) y)
            (liber-points board (conj v [x y]) points color (dec x) y))))
      (if (valid-pos? (inc x) y)
        (if (empty-field? board (inc x) y)
          (if (not (.contains @points [(inc x) y])) 
            (swap! points conj [(inc x) y]))
          (if (same-color? board color (inc x) y)
            (liber-points board (conj v [x y]) points color (inc x) y))))     
      (if (valid-pos? x (dec y))
        (if (empty-field? board x (dec y))
          (if (not (.contains @points [x (dec y)])) 
            (swap! points conj [x (dec y)]))
          (if (same-color? board color x (dec y))
            (liber-points board (conj v [x y]) points color x (dec y)))))
      (if (valid-pos? x (inc y))
        (if (empty-field? board x (inc y))
          (if (not (.contains @points [x (inc y)])) 
            (swap! points conj [x (inc y)]))
          (if (same-color? board color x (inc y))
            (liber-points board (conj v [x y]) points color x (inc y))))))))

(defn liberty-points [board color x y]
  (if (valid-pos? x y)
    (if (same-color? board color x y)
      (let [points (atom '())]
        (liber-points board '() points color x y)
        (count @points))
      401)
    401))

(defn ^:private str-points [board v color x y]
  (if (and (valid-pos? x y) (not (.contains @v [x y])))
    (do
      (if (and
            (valid-pos? (dec x) y)
            (same-color? board color (dec x) y))
        (do
          (if (not (.contains @v [x y])) (swap! v conj [x y]))
          (str-points board v color (dec x) y)))
      (if (and
            (valid-pos? (inc x) y)
            (same-color? board color (inc x) y))
        (do
          (if (not (.contains @v [x y])) (swap! v conj [x y]))
          (str-points board v color (inc x) y)))
      (if (and
            (valid-pos? x (dec y))
            (same-color? board color x (dec y)))
        (do
          (if (not (.contains @v [x y])) (swap! v conj [x y]))
          (str-points board v color x (dec y))))
      (if (and
            (valid-pos?  x (inc y))
            (same-color? board color x (inc y)))
        (do
          (if (not (.contains @v [x y])) (swap! v conj [x y]))
          (str-points board v color x (inc y)))))))

(defn structure-points [board color x y]
  (let [points (atom '())]
    (str-points board points color x y)
    (count @points)))

(defn suicide? [board color x y]
  (and
    (or
      (not-valid-pos? (dec x) y)
      (and
        (occupied? board (dec x) y)
        (if (same-color? board color (dec x) y)
          (no-liberty? (put-piece board color x y) color x y)
          (liberty? (put-piece board color x y) (opposite color) (dec x) y))))
    (or
      (not-valid-pos? (inc x) y)
      (and
        (occupied? board (inc x) y)
        (if (same-color? board color (inc x) y)
          (no-liberty? (put-piece board color x y) color x y)
          (liberty? (put-piece board color x y) (opposite color) (inc x) y))))
    (or
      (not-valid-pos? x (dec y))
      (and
        (occupied? board x (dec y))
        (if (same-color? board color x (dec y))
          (no-liberty? (put-piece board color x y) color x y)
          (liberty? (put-piece board color x y) (opposite color) x (dec y)))))
    (or
      (not-valid-pos? x (inc y))
      (and
        (occupied? board x (inc y))
        (if (same-color? board color x (inc y))
          (no-liberty? (put-piece board color x y) color x y)
          (liberty? (put-piece board color x y) (opposite color) x (inc y)))))))

(defn not-suicide? [board color x y]
  (not (suicide? board color x y)))

(defn suicide-with-benefits? 
  ([board color [x y]] (suicide-with-benefits? board color x y))
  ([board color x y] (and
                       (valid-pos? x y)
                       (empty-field? board x y)
                       (or
                         (not-suicide? board color x y)
                         (or
                           (no-liberty?  (put-piece board color x y) (opposite color) (dec x) y)
                           (no-liberty?  (put-piece board color x y) (opposite color) x (dec y))
                           (no-liberty?  (put-piece board color x y) (opposite color) (inc x) y)
                           (no-liberty?  (put-piece board color x y) (opposite color) x (inc y)))))))
  
;atom used for storing scores
(def black-score (atom 0))
(def white-score (atom 0))

(defn remove-struct-of-color [board color x y]
  (if (and
        (valid-pos? x y)
        (same-color? board color x y))
    (do
      (if (= (opposite color) :white)
        (swap! white-score inc)
        (swap! black-score inc))
      (->
        (remove-piece board x y)
        (remove-struct-of-color color (inc x) y)
        (remove-struct-of-color color x (inc y))
        (remove-struct-of-color color (dec x) y)
        (remove-struct-of-color color x (dec y))))
    board))

(defn remove-if-no-liberty [board color x y]
  (if (and 
        (valid-pos? x y)
        (occupied? board x y)
        (no-liberty? board color x y))
    (remove-struct-of-color board color x y)
    board))

(defn add-piece 
  ([board color [x y]] (add-piece board color x y))
  ([board color x y] (if (suicide-with-benefits? board color x y)
                       (->
                         (put-piece board color x y)
                         (remove-if-no-liberty (opposite color) (dec x) y)
                         (remove-if-no-liberty (opposite color) x (dec y))
                         (remove-if-no-liberty (opposite color) (inc x) y)
                         (remove-if-no-liberty (opposite color) x (inc y)))
                       board)))

;scoring system
(defn color-field [board color x y]
  (cond 
    (= color :white) (assoc-in board [x y] 'O)
    (= color :black) (assoc-in board [x y] 'X)
    :else (assoc-in board [x y] 'M)))

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
            (= ((scoring-board x) y) 'X) (swap! black-score inc)
            (= ((scoring-board x) y) 'O) (swap! white-score inc))
          (recur (dec y))))
      (recur (dec x)))))