(ns clo-go.board)

(def board-size 19)

(defn board []
  (vec (repeat board-size (vec (repeat board-size "-")))))

(defn ^:private put-piece [board color x y]
  (if (= color :white)
    (assoc-in board [x y] "o")
    (assoc-in board [x y] "x")))

(defn piece-at [board x y]
  (cond 
    (= ((board x) y) "x") :black
    (= ((board x) y) "o") :white
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

(defn ^:private remove-piece [board x y]
  (assoc-in board [x y] "-"))

(defn liberty? [board color x y]
  (or
    (or
      (and
        (valid-pos? (dec x) y)
        (empty-field? board (dec x) y))
      (if (same-color? board color (dec x) y)
        (liberty? board color (dec x) y)
        false))
    (or
      (and
        (valid-pos? (inc x) y)
        (empty-field? board (inc x) y))
      (if (same-color? board color (inc x) y)
        (liberty? board color (inc x) y)
        false))
    (or
      (and
        (valid-pos? x (dec y))
        (empty-field? board x (dec y)))
      (if (same-color? board color x (dec y))
        (liberty? board color x (dec y))
        false))
    (or
      (and
        (valid-pos? x (inc y))
        (empty-field? board x (inc y)))
      (if (same-color? board color x (inc y))
        (liberty? board color x (inc y))
        false))))

(defn no-liberty? [board color x y]
  (not (liberty? board color x y)))

(defn suicide? [board color x y]
  (and
    (or
      (not-valid-pos? (dec x) y)
      (if (same-color? color (dec x) y)
        (no-liberty? (put-piece board color x y) color x y)
        (liberty? (put-piece board color x y) color (dec x) y)))
    (or
      (not-valid-pos? (inc x) y)
      (if (same-color? color (inc x) y)
        (no-liberty? (put-piece board color x y) color x y)
        (liberty? (put-piece board color x y) color (inc x) y)))
    (or
      (not-valid-pos? x (dec y))
      (if (same-color? color x (dec y))
        (no-liberty? (put-piece board color x y) color x y)
        (liberty? (put-piece board color x y) color x (dec y))))
    (or
      (not-valid-pos? x (inc y))
      (if (same-color? color x (inc y))
        (no-liberty? (put-piece board color x y) color x y)
        (liberty? (put-piece board color x y) color x (inc y))))))

(defn not-suicide? [board color x y]
  (not (suicide? board color x y)))

(defn suicide-with-benefits? [board color x y]
  (and
    (valid-pos? board x y)
    (empty-field? board x y)
    (or
      (not-suicide? board color x y)
      (and
        (no-liberty? board (opposite color) (dec x) y)
        (no-liberty? board (opposite color) x (dec y))
        (no-liberty? board (opposite color) (inc x) y)
        (no-liberty? board (opposite color) x (inc y))))))
  
(defn remove-struct-of-color [board color x y]
  (if (same-color? board color x y)
    (->
      (remove-piece board x y)
      (remove-struct-of-color color (inc x) y)
      (remove-struct-of-color color x (inc y))
      (remove-struct-of-color color (dec x) y)
      (remove-struct-of-color color x (dec y)))))

(defn remove-if-no-liberty [board color x y]
  (if (and 
        (no-liberty? board color x y)
        (same-color? board color x y))
    (remove-struct-of-color board color x y)
    board))

(defn add-piece [board color x y]
  (if (suicide-with-benefits? board color x y)
    (->
      (put-piece board color x y)
      (remove-if-no-liberty (opposite color) (dec x) y)
      (remove-if-no-liberty (opposite color) x (dec y))
      (remove-if-no-liberty (opposite color) (inc x) y)
      (remove-if-no-liberty (opposite color) x (inc y)))
    (put-piece board color x y)))