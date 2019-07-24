package model

import javax.swing.ImageIcon

/**
  * Icons that will be show on the board for each figure.
  */

trait Images {
  val whitePawnImage = new ImageIcon(getClass.getResource("/images/white_pawn.png"))
  val whiteKnightImage = new ImageIcon(getClass.getResource("/images/white_knight.png"))
  val whiteBishopImage = new ImageIcon(getClass.getResource("/images/white_bishop.png"))
  val whiteRookImage = new ImageIcon(getClass.getResource("/images/white_rook.png"))
  val whiteQueenImage = new ImageIcon(getClass.getResource("/images/white_queen.png"))
  val whiteKingImage = new ImageIcon(getClass.getResource("/images/white_king.png"))
  val blackPawnImage = new ImageIcon(getClass.getResource("/images/black_pawn.png"))
  val blackKnightImage = new ImageIcon(getClass.getResource("/images/black_knight.png"))
  val blackBishopImage = new ImageIcon(getClass.getResource("/images/black_bishop.png"))
  val blackRookImage = new ImageIcon(getClass.getResource("/images/black_rook.png"))
  val blackQueenImage = new ImageIcon(getClass.getResource("/images/black_queen.png"))
  val blackKingImage = new ImageIcon(getClass.getResource("/images/black_king.png"))
}
