(ns clo-go.board-nav-test
  (:require [midje.sweet :refer :all]
            [clo-go.board-nav :refer :all]))

(fact "Up is always x--"
      (up 1 2) => [0 2]
      (up [1 2]) => [0 2]
      (up 0 2) => [-1 2])

(fact "Down is always x++"
      (down 1 2) => [2 2]
      (down [1 2]) => [2 2]
      (down 34 2) => [35 2])

(fact "Left is always y--"
      (left 1 2) => [1 1]
      (left [1 2]) => [1 1]
      (left 0 0) => [0 -1])

(fact "Right is always y++"
      (right 1 2) => [1 3]
      (right [1 2]) => [1 3]
      (right 0 34) => [0 35])