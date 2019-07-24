package model

/**
  * Type of figure. There are 6 in chess: Pawn, Knight, Bishop, Rook, Queen and King.
  */

object FigureType extends Enumeration {
  type FigureType = Value
  val Pawn, Knight, Bishop, Rook, Queen, King = Value
}