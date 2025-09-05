# MChess Engine

This project is an **advanced chess engine** implementing modern optimization techniques for high-performance move calculation. It can evaluate positions efficiently and select the best move in any given scenario.

## Features

- **Alpha-Beta Pruning**  
  Minimizes the number of nodes evaluated in the search tree by cutting off unnecessary branches, optimizing decision-making speed.

- **Bitboard Representation**  
  Uses bitboards to represent piece positions, allowing fast and efficient position processing.

- **Magic Numbers**  
  Implements **Magic Bitboards** for fast calculation of sliding piece moves (rooks and bishops) without long loops.

- **Transposition Table (TT)**  
  Stores previously evaluated positions to avoid recalculating repeated states in the search tree.

- **Move Sorting**  
  Orders moves to maximize the efficiency of alpha-beta pruning, reducing the number of nodes explored.

## Usage

```bash
mvn install compile exec:java -Dexec.mainClass="chessboard.graphics.PlayWithCPU"
```
