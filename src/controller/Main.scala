package controller

import java.awt.FlowLayout

import javax.swing._
import model.PlayerColor
import view.BoardPanel

/**
  * Entry point to the application.
  *
  * Asks the user which figures - white or black - he/she wants to play with, creates the controller and shows the board.
  */

object Main extends App {
  val chooseColor = JOptionPane.showConfirmDialog(null,
    "Do you want to play white figures? If \"no\" you will play with black figures.", "Figures color", JOptionPane.YES_NO_OPTION)
  var controller : Controller = null
  if (chooseColor == JOptionPane.YES_OPTION)
    controller = new Controller(PlayerColor.White)
  else
    controller = new Controller(PlayerColor.Black)
  val boardPanel = new BoardPanel(controller)
  controller.setBoardPanel(boardPanel)
  boardPanel.setVisible(true)
  /* if the player chose black figures -> computer starts  */
  if(chooseColor == JOptionPane.NO_OPTION)
    controller.makeComputerMove()
}