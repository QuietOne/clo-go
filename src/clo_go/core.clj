(ns clo-go.core
  (:require [clo-go.board :refer :all])
  (:require [battle.battles :refer :all]))

(defn help []
  (do
    (println "Welcome to the Clo-Go project")
    (println "=============================")
    (println)
    (println "Insert a number to see a battle")
    (println "1 - Random VS Random")
    (println "2 - Black Random VS White Greedy")
    (println "3 - Greedy VS Greedy")))

(defn ^:private n-battles [battle n]
  (let [black-won (atom 0)
        white-won (atom 0)]
    (loop [x n]
      (when (> x 0)
        (battle)
        (let [winner (who-won 0)]
          (cond
            (= winner :black) (swap! black-won inc)
            (= winner :white) (swap! white-won inc)))
        (recur (dec x))))
    (println "Final results")
    (println "Black:" @black-won)
    (println "White:" @white-won)))

(defn run-battle 
  ([option]
    (cond
      (= option 1) (random-VS-random)
      (= option 2) (black-random-VS-white-greedy)
      (= option 3) (greedy-VS-greedy)
      :else "Option not available"))
  ([option n]
    (cond
      (= option 1) (n-battles random-VS-random n)
      (= option 2) (n-battles black-random-VS-white-greedy n)
      (= option 3) (n-battles greedy-VS-greedy n)
      :else "Option not available")))

(help)