package controller

import javax.swing.{ImageIcon, JLabel, JOptionPane, SwingUtilities}
import model.{Figure, FigureType, Images, PlayerColor}
import model.PlayerColor.PlayerColor
import view.{BoardPanel}
import model.Constants._
import model.FigureType.FigureType
import runnables.moveRunnable

/**
  * Represents the core logic of the game. Initializes game, manipulates the board, executes players and computers moves on the model
  * of the board, handels checkmates and stalemates.
  *
  * @param playerColor - The color of the human player.
  */

case class Controller(val playerColor: PlayerColor = PlayerColor.White) extends Images {

  var boardPanel : BoardPanel = null
  val whiteFigures = createWhiteFigures
  val blackFigures = createBlackFigures

  var currentState = new InternalState(whiteFigures, blackFigures , createBoard(whiteFigures, blackFigures ), PlayerColor.White)

  def getBoard() = currentState.getBoard

  def getBoardPanel() = boardPanel

  def createWhiteFigures(): Vector[Figure] = {

    return Vector[Figure](
      new Figure(FigureType.Queen, PlayerColor.White, 3, 0, new JLabel(whiteQueenImage)),
      new Figure(FigureType.Rook, PlayerColor.White, 7, 0, new JLabel(whiteRookImage)),
      new Figure(FigureType.Rook, PlayerColor.White, 0, 0, new JLabel(whiteRookImage)),
      new Figure(FigureType.Knight, PlayerColor.White, 1, 0, new JLabel(whiteKnightImage)),
      new Figure(FigureType.Knight, PlayerColor.White, 6, 0, new JLabel(whiteKnightImage)),
      new Figure(FigureType.Bishop, PlayerColor.White, 2, 0, new JLabel(whiteBishopImage)),
      new Figure(FigureType.Bishop, PlayerColor.White, 5, 0, new JLabel(whiteBishopImage)),
      new Figure(FigureType.King, PlayerColor.White, 4, 0, new JLabel(whiteKingImage)),
      new Figure(FigureType.Pawn, PlayerColor.White, 0, 1, new JLabel(whitePawnImage)),
      new Figure(FigureType.Pawn, PlayerColor.White, 1, 1, new JLabel(whitePawnImage)),
      new Figure(FigureType.Pawn, PlayerColor.White, 2, 1, new JLabel(whitePawnImage)),
      new Figure(FigureType.Pawn, PlayerColor.White, 3, 1, new JLabel(whitePawnImage)),
      new Figure(FigureType.Pawn, PlayerColor.White, 4, 1, new JLabel(whitePawnImage)),
      new Figure(FigureType.Pawn, PlayerColor.White, 5, 1, new JLabel(whitePawnImage)),
      new Figure(FigureType.Pawn, PlayerColor.White, 6, 1, new JLabel(whitePawnImage)),
      new Figure(FigureType.Pawn, PlayerColor.White, 7, 1, new JLabel(whitePawnImage))
    )
  }

  def createBlackFigures(): Vector[Figure] = {

    return Vector[Figure](
      new Figure(FigureType.Queen, PlayerColor.Black, 3, 7, new JLabel(blackQueenImage)),
      new Figure(FigureType.Rook, PlayerColor.Black, 7, 7, new JLabel(blackRookImage)),
      new Figure(FigureType.Rook, PlayerColor.Black, 0, 7, new JLabel(blackRookImage)),
      new Figure(FigureType.Knight, PlayerColor.Black, 1, 7, new JLabel(blackKnightImage)),
      new Figure(FigureType.Knight, PlayerColor.Black, 6, 7, new JLabel(blackKnightImage)),
      new Figure(FigureType.Bishop, PlayerColor.Black, 2, 7, new JLabel(blackBishopImage)),
      new Figure(FigureType.Bishop, PlayerColor.Black, 5, 7, new JLabel(blackBishopImage)),
      new Figure(FigureType.King, PlayerColor.Black, 4, 7, new JLabel(blackKingImage)),
      new Figure(FigureType.Pawn, PlayerColor.Black, 0, 6, new JLabel(blackPawnImage)),
      new Figure(FigureType.Pawn, PlayerColor.Black, 1, 6, new JLabel(blackPawnImage)),
      new Figure(FigureType.Pawn, PlayerColor.Black, 2, 6, new JLabel(blackPawnImage)),
      new Figure(FigureType.Pawn, PlayerColor.Black, 3, 6, new JLabel(blackPawnImage)),
      new Figure(FigureType.Pawn, PlayerColor.Black, 4, 6, new JLabel(blackPawnImage)),
      new Figure(FigureType.Pawn, PlayerColor.Black, 5, 6, new JLabel(blackPawnImage)),
      new Figure(FigureType.Pawn, PlayerColor.Black, 6, 6, new JLabel(blackPawnImage)),
      new Figure(FigureType.Pawn, PlayerColor.Black, 7, 6, new JLabel(blackPawnImage))
    )
  }

  def createBoard(w : Vector[Figure], b : Vector[Figure]): Vector[Vector[Figure]] = {

    val t = Vector[Vector[Figure]](
      Vector[Figure](w(2), w(8), null, null, null, null, b(8), b(2)),
      Vector[Figure](w(3),w(9), null, null, null, null, b(9), b(3)),
      Vector[Figure](w(5),w(10),null,null,null,null,b(9),b(5)),
      Vector[Figure](w(0),w(11),null,null,null,null,b(11),b(0)),
      Vector[Figure](w(7),w(12),null,null,null,null,b(12),b(7)),
      Vector[Figure](w(6),w(13),null,null,null,null,b(13),b(6)),
      Vector[Figure](w(4),w(14),null,null,null,null,b(14),b(4)),
      Vector[Figure](w(1),w(15),null,null,null,null,b(15),b(1)))
    return t
  }

  def getOpponentColor(playerColor: PlayerColor): PlayerColor = {
    playerColor match {
      case PlayerColor.Black => PlayerColor.White
      case PlayerColor.White => PlayerColor.Black
    }
  }

  def getOpponentColor(): PlayerColor = {
    if(playerColor == PlayerColor.White)
      PlayerColor.Black
    PlayerColor.White
  }

  def stalemate(color: PlayerColor) = {
    boardPanel.repaintFigures()
    if(boardPanel.computerVsComputerRunnable != null)
      boardPanel.setComputerVsComputerRunFlag(false)
    println("Stalemate - nobody has won.")
    JOptionPane.showMessageDialog(null, "Stalemate - nobody has won.")
    boardPanel.finish
  }

  /**
    * Update the internal state of the game according to the move that the human player does.
    *
    * @param figure The figure to be moved.
    * @param destination The (x,y) destination position of the @figure.
    */
	def makePlayerMove(figure : Figure, destination: (Int, Int)) : Unit = {
    currentState = currentState.makeMove(figure, destination)
    if(currentState.getChildren().isEmpty && currentState.isKingAttacked(currentState.activePlayer)){
      win(currentState.getOpponentColor(currentState.activePlayer))
      return
    }
    if(currentState.getChildren().isEmpty && !currentState.isKingAttacked(currentState.activePlayer)){
      stalemate(currentState.getOpponentColor(currentState.activePlayer))
      return
    }
    new Thread(new moveRunnable(this)).start()
	}

  def makeComputerMove() : Unit = {
    new Thread(new Runnable {
      override def run(): Unit = {
        val minimax = new Algorithm(currentState.copy(), boardPanel.getEnemyDepth)
        val move = minimax.run()
        currentState = currentState.makeMove(move)
        if(currentState.getChildren().isEmpty && currentState.isKingAttacked(currentState.activePlayer)){
          win(currentState.getOpponentColor(currentState.activePlayer))
          return
        }
        if(currentState.getChildren().isEmpty && !currentState.isKingAttacked(currentState.activePlayer)){
          stalemate(currentState.getOpponentColor(currentState.activePlayer))
          return
        }
        SwingUtilities.invokeAndWait(new Runnable {
          override def run(): Unit = {
            boardPanel.repaintFigures()
            boardPanel.enableFigures()
          }
        })
      }
    }).start()
  }

  def computerVsComputer() : Unit = {
    val minimaxFirst = new Algorithm(currentState.copy(), boardPanel.getPlayerDepth)
    val firstMove = minimaxFirst.run()
    currentState = currentState.makeMove(firstMove)
    if(currentState.getChildren().isEmpty && currentState.isKingAttacked(currentState.activePlayer)){
      win(currentState.getOpponentColor(currentState.activePlayer))
      return
    }
    if(currentState.getChildren().isEmpty && !currentState.isKingAttacked(currentState.activePlayer)){
      stalemate(currentState.getOpponentColor(currentState.activePlayer))
      return
    }

    boardPanel.repaintFigures()

    val minimaxSecond = new Algorithm(currentState.copy(), boardPanel.getEnemyDepth)
    val secondMove = minimaxSecond.run()
    currentState = currentState.makeMove(secondMove)
    if(currentState.getChildren().isEmpty && currentState.isKingAttacked(currentState.activePlayer)){
      win(currentState.getOpponentColor(currentState.activePlayer))
      return
    }
    if(currentState.getChildren().isEmpty && !currentState.isKingAttacked(currentState.activePlayer)){
      stalemate(currentState.getOpponentColor(currentState.activePlayer))
      return
    }
    boardPanel.repaintFigures()
  }

  def win(color : PlayerColor): Unit = {
    boardPanel.repaintFigures()
    if(boardPanel.computerVsComputerRunnable != null)
      boardPanel.setComputerVsComputerRunFlag(false)
    println(currentState.getOpponentColor(currentState.activePlayer) + " has won.")
    JOptionPane.showMessageDialog(null, color.toString + " has won.")
    boardPanel.finish
  }

  def findPossibleMoves(figure : Figure) = currentState.findPossibleMoves(figure)

  def isPlayersMove : Boolean = currentState.activePlayer == playerColor

  def getBlackFigures: Vector[Figure] = currentState.getBlackFigures

  def getWhiteFigures: Vector[Figure] = currentState.getWhiteFigures

  def setBoardPanel(_boardPanel : BoardPanel) = boardPanel = _boardPanel
}
