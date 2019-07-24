package controller

import model.PlayerColor.PlayerColor
import model.{Figure, FigureType, Images, PlayerColor}

import scala.annotation.tailrec
import scala.collection.immutable.VectorBuilder

/**
  * Represents the situation on the board including the set of white figures, the set of black figures and the general
  * state of the board.
  *
  * @param whiteFigures Set of white figures.
  * @param blackFigures Set of black figures.
  * @param board 8x8 representation of the board that is implied by @blackFigures and @whiteFigures.
  * @param activePlayer The player that makes a move.
  */

case class InternalState(val whiteFigures : Vector[Figure], val blackFigures : Vector[Figure], val board: Vector[Vector[Figure]],
                         val activePlayer : PlayerColor) extends Images {

  def getActiveFigures()  : Vector[Figure] = return getFigures(activePlayer)

  def getMoves(figure : Figure) : Vector[(Figure, (Int, Int))] = {
    findPossibleMoves(figure).map(e => (figure, e))
  }
  def getWhiteFigures() : Vector[Figure] = whiteFigures
  def getBlackFigures() : Vector[Figure] = blackFigures
  def getFigures(color : PlayerColor) : Vector[Figure] = {
    color match {
      case PlayerColor.White => getWhiteFigures
      case PlayerColor.Black => getBlackFigures
    }
  }

  def getBoard : Vector[Vector[Figure]] = board

  def getOpponentColor(color : PlayerColor) : PlayerColor = {
    color match {
      case PlayerColor.White => PlayerColor.Black
      case PlayerColor.Black => PlayerColor.White
    }
  }

  def findAllCoveredFields(color : PlayerColor) : Vector[(Int, Int)] = {
    return getFigures(color).map(f => findCoveredFields(f)).reduce(_ ++ _)
  }

  def findPawnAttackMoves(pawn : Figure): Vector[(Figure, (Int, Int))] = {
    pawn.getColor() match {
      case PlayerColor.White => return whitePawnAttackMoves(pawn).map(m => (pawn, m))
      case PlayerColor.Black => return blackPawnAttackMoves(pawn).map(m => (pawn, m))
    }
    return Vector[(Figure, (Int, Int))]()
  }

	def isKingAttacked(kingColor : PlayerColor) : Boolean = {
		val king = getFigures(kingColor).filter(f => f.getType() == FigureType.King)(0)
    val enemyMoves = findAllCoveredFields(getOpponentColor(kingColor))
    if(enemyMoves.contains(king.XY))
      return true
    return false
	}

  def findCoveredFields(figure : Figure) : Vector[(Int, Int)] = {
    val possibleMoves =
      figure.getType() match {
        case FigureType.Bishop => findBishopPossibleMoves(figure)
        case FigureType.Knight => findKnightPossibleMoves(figure)
        case FigureType.Pawn => {
          figure.getColor() match {
            case PlayerColor.White => whitePawnAttackMoves(figure)
            case PlayerColor.Black => blackPawnAttackMoves(figure)
          }
        }
        case FigureType.Queen => findQueenPossibleMoves(figure)
        case FigureType.Rook => findRookPossibleMoves(figure)
        case FigureType.King => findKingPossibleMoves(figure)
      }
    return possibleMoves
  }

  def findPossibleMoves(figure : Figure) : Vector[(Int, Int)] = {
		val possibleMoves =
    figure.getType() match {
      case FigureType.Bishop => findBishopPossibleMoves(figure)
      case FigureType.Knight => findKnightPossibleMoves(figure)
      case FigureType.Pawn => findPawnPossibleMoves(figure)
      case FigureType.Queen => findQueenPossibleMoves(figure)
      case FigureType.Rook => findRookPossibleMoves(figure)
      case FigureType.King => findKingPossibleMoves(figure)
    }
    return possibleMoves.filterNot(m => makeMove(figure, m).isKingAttacked(figure.getColor()))
	}

  def whitePawnAttackMoves(pawn : Figure) : Vector[(Int, Int)] = {
    val moves = Vector[(Int, Int)]((pawn.x-1, pawn.y+1), (pawn.x+1, pawn.y+1))
    return moves.filter(m => m._1 > 0 && m._1 < 8 && m._2 > 0 && m._2 < 8)
  }

  def blackPawnAttackMoves(pawn : Figure) : Vector[(Int, Int)] = {
    val moves = Vector[(Int, Int)]((pawn.x-1, pawn.y-1), (pawn.x+1, pawn.y-1))
    return moves.filter(m => m._1 > 0 && m._1 < 8 && m._2 > 0 && m._2 < 8)
  }

  def findPawnPossibleMoves(figure : Figure) : Vector[(Int, Int)] = {
    val possibleMoves = new VectorBuilder[(Int, Int)]

    figure.getColor match {
      case PlayerColor.Black => {
        if(figure.y > 0 && board(figure.x)(figure.y-1) == null)
          possibleMoves += ((figure.x, figure.y-1))
        if(figure.y == 6 && board(figure.x)(5) == null && board(figure.x)(4) == null)
          possibleMoves += ((figure.x, 4))
        if(figure.x > 0 && figure.y > 0 && board(figure.x-1)(figure.y-1) != null && board(figure.x-1)(figure.y-1).getColor == getOpponentColor(figure.getColor))
          possibleMoves += ((figure.x-1, figure.y-1))
        if(figure.x < 7 && figure.y > 0 && board(figure.x+1)(figure.y-1) != null && board(figure.x+1)(figure.y-1).getColor == getOpponentColor(figure.getColor))
          possibleMoves += ((figure.x+1, figure.y-1))
      }
      case PlayerColor.White => {
        if(figure.y < 7 && board(figure.x)(figure.y+1) == null)
          possibleMoves += ((figure.x, figure.y+1))
        if(figure.y == 1 && board(figure.x)(2) == null && board(figure.x)(3) == null)
          possibleMoves += ((figure.x, 3))
        if(figure.x > 0 && figure.y < 7 && board(figure.x-1)(figure.y+1) != null && board(figure.x-1)(figure.y+1).getColor == getOpponentColor(figure.getColor))
          possibleMoves += ((figure.x-1, figure.y+1))
        if(figure.x < 7 && figure.y < 7 && board(figure.x+1)(figure.y+1) != null && board(figure.x+1)(figure.y+1).getColor == getOpponentColor(figure.getColor))
          possibleMoves += ((figure.x+1, figure.y+1))
      }
    }
    possibleMoves.result()
  }

  def findKnightPossibleMoves(f : Figure) : Vector[(Int, Int)] = {
    val moves = (for(x <- Vector[Int](f.x - 1, f.x + 1); y <- Vector[Int](f.y - 2, f.y + 2)) yield (x, y)).++(for(x <- Vector[Int](f.x - 2, f.x + 2); y <- Vector[Int](f.y - 1, f.y + 1)) yield (x, y))
    return moves.filter(e => 8 > e._1 && e._1 >= 0 && e._2 < 8 && e._2 >= 0 && (getFigure(e) == null || getFigure(e).getColor() == getOpponentColor(f.getColor()))).toVector
  }

  def getFigure(e : (Int, Int)) : Figure = board(e._1)(e._2)
  def getFigure(x : Int, y : Int) : Figure = board(x)(y)

  def findKingPossibleMoves(f : Figure) : Vector[(Int, Int)] = {
    val moves = for(x <- Vector[Int](f.x - 1, f.x, f.x + 1); y <- Vector[Int](f.y-1, f.y, f.y + 1)) yield (x, y)
    return moves.filter(e => 8 > e._1 && e._1 >= 0 && e._2 < 8 && e._2 >= 0 && (getFigure(e) == null || getFigure(e).getColor() == getOpponentColor(f.getColor()))).toVector
  }

  /**
    * Returns a new internal state with figure sets and board updated according to the given parameters that represent the move.
    */
  def makeMove(x : (Figure, (Int, Int))) : InternalState = {
    makeMove(x._1, x._2)
  }

  def makeMove(figure : Figure, destination: (Int, Int)) : InternalState = {

    val newFigure = new Figure(figure.getType, figure.getColor, destination._1, destination._2, figure.getFigureImage)

    val t1 = board(figure.x).updated(figure.y, null)
    val t2 = board.updated(figure.x, t1)
    val t3 = t2(destination._1).updated(destination._2, newFigure)
    val newBoard = t2.updated(destination._1, t3)

    if(figure.getColor == PlayerColor.White){
      val newWhiteFigures = whiteFigures.filterNot(f => f.x == figure.x && f.y == figure.y) :+ newFigure
      return new InternalState(newWhiteFigures,
        if(board(destination._1)(destination._2) != null && board(destination._1)(destination._2).getColor() != figure.getColor())
          getBlackFigures.filterNot(f => f.x == destination._1 && f.y == destination._2)
        else
          getBlackFigures,
        newBoard,
        getOpponentColor(figure.getColor()))

    }
    else{
      val newBlackFigures = blackFigures.filterNot(f => f.x == figure.x && f.y == figure.y) :+ newFigure
      return new InternalState(
        if(board(destination._1)(destination._2) != null && board(destination._1)(destination._2).getColor() != figure.getColor())
          getWhiteFigures.filterNot(f => f.x == destination._1 && f.y == destination._2)
        else
          getWhiteFigures,
        newBlackFigures,
        newBoard,
        getOpponentColor(figure.getColor()))
    }
  }

  // function which tries to add consistently all possible moves in direction chosen by h - horizontal factor and v - vertical factor, v and h can be only 3 values (-1, 0, 1)
  def addAllPossibleMovesInDirection(figure : Figure, h : Int, v : Int) : Vector[(Int, Int)] = {

    @tailrec
    def recursion(i : Int, vector : Vector[(Int, Int)]) : Vector[(Int, Int)] = {
      val x = figure.x + h * i
      val y = figure.y + v * i

      if(0 <= x && x <= 7 && 0 <= y && y <= 7)
      {
        if(board(x)(y) == null)
          recursion(i+1, vector :+ (x, y))
        else if(board(x)(y).getColor == getOpponentColor(figure.getColor))
          vector :+ (x, y)
        else if(board(x)(y).getColor == figure.getColor)
          vector
        else
          vector
      }
      else
        vector
    }

    recursion(1, Vector.empty[(Int, Int)])
  }

  def findBishopPossibleMoves(figure : Figure) : Vector[(Int, Int)] = {
    val possibleMoves = new VectorBuilder[(Int, Int)]

    possibleMoves ++= addAllPossibleMovesInDirection(figure, 1, 1); // up right
    possibleMoves ++= addAllPossibleMovesInDirection(figure, 1, -1); // down right
    possibleMoves ++= addAllPossibleMovesInDirection(figure, -1, -1); // down left
    possibleMoves ++= addAllPossibleMovesInDirection(figure, -1, 1); // up left

    possibleMoves.result()
  }

  def findRookPossibleMoves(figure : Figure) : Vector[(Int, Int)] = {
    val possibleMoves = new VectorBuilder[(Int, Int)]

    possibleMoves ++= addAllPossibleMovesInDirection(figure, 0, 1); // up
    possibleMoves ++= addAllPossibleMovesInDirection(figure, 1, 0); // right
    possibleMoves ++= addAllPossibleMovesInDirection(figure, 0, -1); // down
    possibleMoves ++= addAllPossibleMovesInDirection(figure, -1, 0); // left

    possibleMoves.result()
  }

  def findQueenPossibleMoves(figure : Figure) : Vector[(Int, Int)] = {
    val possibleMoves = new VectorBuilder[(Int, Int)]

    possibleMoves ++= addAllPossibleMovesInDirection(figure, 0, 1); // up
    possibleMoves ++= addAllPossibleMovesInDirection(figure, 1, 1); // up right
    possibleMoves ++= addAllPossibleMovesInDirection(figure, 1, 0); // right
    possibleMoves ++= addAllPossibleMovesInDirection(figure, 1, -1); // down right
    possibleMoves ++= addAllPossibleMovesInDirection(figure, 0, -1); // down
    possibleMoves ++= addAllPossibleMovesInDirection(figure, -1, -1); // down left
    possibleMoves ++= addAllPossibleMovesInDirection(figure, -1, 0); // left
    possibleMoves ++= addAllPossibleMovesInDirection(figure, -1, 1); // up left

    possibleMoves.result()
  }

  def getAllMoves() : Vector[(Figure, (Int, Int))] = {

    getActiveFigures().map(f => getMoves(f)).reduce(_ ++ _)
  }

  def getAllMoves(color : PlayerColor) : Vector[(Figure, (Int, Int))] = {

    getFigures(color).map(f => getMoves(f)).reduce(_ ++ _)
  }

  def getChildren() : Vector[InternalState] = {

    val moves = getActiveFigures().map(f => getMoves(f)).reduce(_ ++ _)
    moves.map(m => makeMove(m))
  }
}
