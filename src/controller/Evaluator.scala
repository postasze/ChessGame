package controller

import model.PlayerColor.PlayerColor
import model.{Figure, FigureType}

/**
  * Represents general algorithm to evaluate the state of the board from the maximizing players perspective.
  * Gives clue how good the situtation on the board is for the maximizing player.
  *
  * @param maximizing The color of the player whose score we want to maximize.
  */
class Evaluator(val maximizing : PlayerColor) {

  val pawnTable : Array[Array[Int]] = Array(
    Array(0, 0, 0, 0, 0, 0, 0, 0),
    Array(1, 1, 1, 1, 1, 1, 1, 1),
    Array(1, 2, 2, 2, 2, 2, 2, 1),
    Array(1, 2, 2, 3, 3, 2, 2, 1),
    Array(1, 2, 2, 3, 3, 2, 2, 1),
    Array(1, 2, 2, 2, 2, 2, 2, 1),
    Array(1, 1, 1, 1, 1, 1, 1, 1),
    Array(0, 0, 0, 0, 0, 0, 0, 0)
  )
  val knightTable : Array[Array[Int]] = Array(
    Array(3, 3, 5, 5, 5, 5, 3, 3),
    Array(3, 5, 6, 6, 6, 6, 5, 3),
    Array(5, 6, 8, 8, 8, 8, 5, 5),
    Array(5, 6, 8, 9, 9, 8, 6, 5),
    Array(5, 6, 8, 9, 9, 8, 6, 5),
    Array(5, 6, 8, 8, 8, 8, 5, 5),
    Array(3, 5, 6, 6, 6, 6, 5, 3),
    Array(3, 3, 5, 5, 5, 5, 3, 3)
  )
  val bishopTable : Array[Array[Int]] = Array(
    Array(5, 5, 5, 5, 5, 5, 5, 5),
    Array(5, 6, 6, 6, 6, 6, 6, 5),
    Array(5, 6, 6, 7, 7, 6, 6, 5),
    Array(5, 6, 7, 9, 9, 7, 6, 5),
    Array(5, 6, 7, 9, 9, 7, 6, 5),
    Array(5, 6, 6, 7, 7, 6, 6, 5),
    Array(5, 6, 6, 6, 6, 6, 6, 5),
    Array(5, 5, 5, 5, 5, 5, 5, 5)
  )
  val rookTable : Array[Array[Int]] = Array(
    Array(9, 9, 9, 9, 9, 9, 9, 9),
    Array(9,10,10,10,10,10,10, 9),
    Array(9,10,10,10,10,10,10, 9),
    Array(9,10,10,12,12,10,10, 9),
    Array(9,10,10,12,12,10,10, 9),
    Array(9,10,10,10,10,10,10, 9),
    Array(9,10,10,10,10,10,10, 9),
    Array(9, 9, 9, 9, 9, 9, 9, 9)
  )
  val queenTable : Array[Array[Int]] = Array(
    Array(12, 12, 12, 12, 12, 12, 12, 12),
    Array(12, 15, 16, 16, 16, 16, 15, 12),
    Array(12, 15, 18, 20, 20, 18, 15, 12),
    Array(12, 15, 20, 20, 20, 20, 15, 12),
    Array(12, 15, 20, 20, 20, 20, 15, 12),
    Array(12, 15, 18, 20, 20, 18, 15, 12),
    Array(12, 15, 16, 16, 16, 16, 15, 12),
    Array(12, 12, 12, 12, 12, 12, 12, 12),
  )
  val kingTable : Array[Array[Int]] = Array(
    Array(12, 12, 12, 12, 12, 12, 12, 12),
    Array(5, 5, 5, 5, 5, 5, 5, 5),
    Array(0, 0, 0, 0, 0, 0, 0, 0),
    Array(0, 0, 0, 0, 0, 0, 0, 0),
    Array(0, 0, 0, 0, 0, 0, 0, 0),
    Array(0, 0, 0, 0, 0, 0, 0, 0),
    Array(5, 5, 5, 5, 5, 5, 5, 5),
    Array(12, 12, 12, 12, 12, 12, 12, 12)
  )

  /**
    * Evaluates the score of a single figure according to its type and position.
    * @param figure Figure which position is to be evaluated.
    * @return The score of the given figure.
    */

  def evaluate(figure : Figure) : Int = {
    figure.figureType match {
      case FigureType.King => return kingTable(figure.x)(figure.y)
      case FigureType.Pawn => return pawnTable(figure.x)(figure.y)
      case FigureType.Bishop => return bishopTable(figure.x)(figure.y)
      case FigureType.Knight => return knightTable(figure.x)(figure.y)
      case FigureType.Rook => return rookTable(figure.x)(figure.y)
      case FigureType.Queen => return  queenTable(figure.x)(figure.y)
    }
  }

  /**
    * Evaluates the whole state of the board. The more the maximizing player has figures that are placed in good positions
    * the better score he/she gets. The score of a single figure on the board is based on its position according to the
    * score tables for each figure type.
    *
    * @param internalState State to be evaluated.
    * @return Evaluation of the state of the board from the perspective of the maximizing player.
    */
  def evaluateState(internalState : InternalState) : Int = {
    val minimizing = internalState.getOpponentColor(maximizing)
    return internalState.getFigures(maximizing).map(f => if(f == null) 0 else evaluate(f)).sum -
      internalState.getFigures(minimizing).map(f => if(f == null) 0 else evaluate(f)).sum
  }
}
