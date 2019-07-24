package model

import javax.swing.JLabel
import model.FigureType._
import model.PlayerColor._

/**
  * Represents a figure.
  *
  * @param figureType The type of the figure. Pawn/bishop/knight/rook/king/queen.
  * @param color The color of the figure. White/Black.
  * @param x X position of the figure on the board.
  * @param y Y position of the figure on the board.
  * @param figureImage The image of the figure that will be show on the board.
  */

case class Figure(val figureType : FigureType, val color : PlayerColor, val x : Int, val y: Int,
	val figureImage : JLabel) {

  def getFigureImage: JLabel = figureImage
  def getPoint: (Int, Int) = (x, y)
  def getX: Int = x
  def getY: Int = y
  def XY() : (Int, Int) = (x, y)
  def getType(): FigureType = figureType
  def getColor(): PlayerColor = color
}
