(ns clo-go.board-struct)
;basic API operations on structure that represents a board

(def board-size 19)

(defn board []
  (vec (repeat board-size (vec (repeat board-size '-)))))

(defn put-piece
  ([board color pos]
    (if (= color :white)
      (assoc-in board pos 'o)
      (assoc-in board pos 'x)))
  ([board color x y]
    (put-piece board color [x y])))

(defn piece-at
  ([board [x y]] (piece-at board x y))
  ([board x y]
    (let [piece ((board x) y)]
      (cond 
        (= piece 'x) :black
        (= piece 'o) :white
        :else :empty))))

(defn same-color?
  ([board color [x y]] (same-color? board color x y))
  ([board color x y] (= color (piece-at board x y)))
  ([board x y x1 y1] (= (piece-at board x y) (piece-at board x1 y1))))

(defn not-same-color? [board x y x1 y1]
  (not (same-color? board x y x1 y1)))

(defn opposite [color]
  (cond
    (= color :white) :black
    (= color :black) :white
    :else :empty))

(defn empty-field? 
  ([board [x y]] (empty-field? board x y))
  ([board x y] (= (piece-at board x y) :empty)))

(defn valid-pos?
  ([[x y]] (valid-pos? x y))
  ([x y] (and
           (< x board-size)
           (< y board-size)
           (>= x 0)
           (>= y 0))))

(defn not-valid-pos? 
  ([pos] (not (valid-pos? pos)))
  ([x y] (not (valid-pos? x y))))

(defn occupied? 
  ([board [x y]] (occupied? board x y))
  ([board x y] (or
                 (not-valid-pos? x y)
                 (not (empty-field? board x y)))))

(defn remove-piece [board pos]
  (assoc-in board pos '-))

;API used for coloring when the game is finished
(defn color-field 
  ([board color [x y]]
    (color-field board color x y))
  ([board color x y]
    (cond 
      (= color :white) (assoc-in board [x y] 'O)
      (= color :black) (assoc-in board [x y] 'X)
      :else (assoc-in board [x y] 'M))))

(defn territory-color [scoring-board x y]
  (let [color ((scoring-board x) y)]
    (cond
      (= color 'X) :black
      (= color 'O) :white
      (= color 'M) :no-mans-land
      :else :not-territory)))