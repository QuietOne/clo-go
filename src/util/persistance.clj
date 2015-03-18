(ns util.persistance
  (:require [clojure.java.io :refer :all]))

(defn persist-results [black-score white-score board battle-name]
  (with-open [wrtr (writer (str battle-name ".txt") :append true)]
    (.write wrtr (str black-score " " white-score " " board "\n"))))