# clo-go

Attempt to make different AIs for Go with Clojure.

##AI

###Monte Carlo AI

Monte Carlo AI is functional and will play randomized possible moves on board, without any strategy.

###Greedy AI

Greedy AI is functional and will play the moves that have the greatest payoff as soon as possible.

##Battles and Results

The score is represented as number of captured pieces and territories.

####Note
Handicap points have not been included.
Removing the dead structures has not been implemented.


###Monte Carlo AI VS Monte Carlo AI

There are no definitive winner. It purely random who will be the winner. From resulting data, it can be concluded that there are two resulting game board settings when AIs "decides" to not take another move:

1. Highly populated game board
```
Black score: 7
White score: 15
[[x - o o o o o o x o o o x x x x o - -] 
 [o x x o o o o o x o - o o - o - - o o] 
 [- - - o x - x o x x x x - o o o - - o] 
 [x x x x x - o x x o x o - o o x x x x] 
 [- x o x o - x x x o x o x o x x o - -] 
 [x - o o - x o - x o o o - x x o x o o] 
 [x - - - o x x x x o o - x - - o - o x] 
 [x - - - o - x x - o - - x x x - o o -] 
 [o o o o o - o x x x o x - x - - x o o] 
 [- x o o x x o o x - x x - o o x - o x] 
 [o - o o o o - x x x x x o - o - o o -] 
 [x x x x x o x x - o - o - o - - o x x] 
 [- x - o - x x o x x - o o - o x x - x] 
 [x o o o - x x - o x x x o o - o - o o]
 [x o x o x o x o o - x - x - x x o x -]
 [o - x o - o x x o x o x o - o o - x o]
 [- o - x x o o o o x o - o x x o o o -] 
 [o o x - x - x o - - o x - - - x o - x] 
 [x x - x x - o o - x - - x o o - o o x]]

Black score: 20
White score: 18
[[o - o o o o o o o o - o x x o o o o o]
 [x - o x x x x x o o o o x o o o o - -] 
 [o x o - x x x - - - o x x x o o o o -] 
 [o o x o o x x x x x x o - x - o o - o] 
 [x o x o x x x x o x o o x - o - o x o] 
 [- o x x x - o x o - o x x x o o x - o] 
 [x o - x x o x x o o x x x x - o o x -] 
 [x x o x x o - x o o x x o - - - - o x] 
 [x x x o o x - o o o o x o o x o x o -] 
 [o o o o x x x x o o - o x o o x - x o] 
 [o - x o o o x x - o x x x o o x o - x] 
 [x - - o x - x x o x x x - x x x o x x] 
 [x o x x x o x x x o - - x x x - x - x] 
 [x x o o o o x x x - o o x x x x x - x] 
 [x x o o o - o - x x x x o o x o x x -] 
 [o o o o o o x x - x o o o o o o x o x] 
 [x x x x o x - x x o o o - x o o o - x] 
 [x x o - x x x x - o o - o o o o o o x] 
 [x x x x - o x o o - o o x - o o o o x]]
```

2. Sparsely populated game board

```
Black score: 2
White score: 2
[[o - - - o - - o x - o - x o - x x o -]
 [x o - - - - o - - o - - - - x x o o o] 
 [x - x x x o x - - o - x x o o x - x x] 
 [- x o x - o - o - - o o - o x - - - o] 
 [o - - - o o x o x - - x o o - o x o o] 
 [- o x x o x o - o x x - - o - x - x x] 
 [- x - x x x - x - - - x x - o - x - -] 
 [o x - o - - o x x - - o x x x o x x o] 
 [- - o - - x x x - - x o - - - - - x x] 
 [o o x - - x - - x x - - o - o - - - o] 
 [o x o x - x - - o - o x - o - o x - x] 
 [- - - x - - o - - o - - o - x x o o o] 
 [x o - - x - o o x - x - x - - - x o x]
 [x o o o x x o x o - o o - - o x - x -] 
 [x x - x o x o x x x o x - o o x x x -] 
 [x o x - - o o o - - o - - - - o x - -] 
 [x o - - o x - - x - - o - o x - - - x] 
 [- - o o x x x x x - o o - - - - o - o] 
 [o - o - - o - - x - o - o - - o o - o]]

Black score: 7
White score: 4
[[o x o o x - x - x - x - o - o - o - o]
 [o - x - x - x x x o o o o - - - - x o] 
 [x - o o - x o o - - x o o x - x - - o] 
 [- - o - o - o - - - x - x o x - - o -] 
 [x - - x x x x x o x - o - o o - x - o] 
 [x - o o o o - x - x x o - - - - - - -] 
 [- - o - - x o x - o - o x x - x - o -] 
 [- x - o - o - x o - x - - x x o x o -] 
 [- o o - x - o x o - x - o x x - o o -] 
 [- - o - o - - x x - - - x - x x o x -] 
 [o o - - x - o o x - x o - x x x - o -] 
 [x - o - x x o - - o x - o - x o x x o] 
 [o o - - o o o o - - x o - - o o o x o] 
 [- o o x - - o - - x x x - - x - x o -] 
 [- o x x x o o o x - x - x - - - o - -] 
 [- x - o x x - - - x x o - x x o x - o]
 [- x x - x - o x x - x - - o - x - o -] 
 [o o x - - x x x x - x - o o - - - x o] 
 [o - o o - o - - o x o - - - x o - x -]]
```
 
## License

Copyright © 2015

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
