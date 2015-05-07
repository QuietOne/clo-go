(ns clo-go.board-nav)

(defn ^:private nav-x 
  ([f x y] [(f x) y])
  ([f [x y]] [(f x) y]))

(defn ^:private nav-y
  ([f x y] [x (f y)])
  ([f [x y]] [x (f y)]))

(defn up 
  ([pos] (nav-x dec pos))
  ([x y] (nav-x dec x y)))

(defn down 
  ([pos] (nav-x inc pos))
  ([x y] (nav-x inc x y)))

(defn left
  ([pos] (nav-y dec pos))
  ([x y] (nav-y dec x y)))

(defn right
  ([pos] (nav-y inc pos))
  ([x y] (nav-y inc x y)))