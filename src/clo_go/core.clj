(ns clo-go.core
  (:require [clo-go.board :refer :all])
  (:require [battle.battles :refer :all]))

(defn help []
  (do
    (println "Welcome to the Clo-Go project")
    (println "=============================")
    (println)
    (println "To see a battle write a command in REPL (run-battle battle-num),")
    (println "where battle-num is number of selected battle:")
    (println "1 - Random VS Random")
    (println "2 - Black Random VS White Greedy")
    (println "3 - Greedy VS Greedy")
    (println)
    (println "If you want to see more battles than 1, write command in REPL")
    (println "(run-battle battle-num battle-count)")
    (println)
    (println "It is recommended to narrow REPL window for better experience, as")
    (println "the board is not formated for printing")
    (println "=============================")
    (println "\t\t\tTihomir Radosavljevic")))

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