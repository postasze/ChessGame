package controller

import model.{Figure, PlayerColor}
import model.PlayerColor.PlayerColor

import scala.annotation.tailrec

/**
  * Implementation of the [[https://en.wikipedia.org/wiki/Minimax minimax algorithm]] with [[https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning alpha-beta pruning]].
  *
  * @param initialState State for which we want to calculate the next move.
  * @param depth Number of recursive calls of the algorithm. I. e. how many moves will be simulated.
  */

class Algorithm(val initialState : InternalState, val depth : Int = 2) {

  val maximizing = initialState.activePlayer
  val evaluator = new Evaluator(maximizing)
  val INFINITY = 1000000


  @tailrec
  private def minIteration(alpha : Int, beta : Int, height : Int, i : Int, states: Vector[InternalState]) : Int = {
    val newBeta = max(alpha, beta, height - 1, states(i))
    if(newBeta <= alpha)
      return alpha
    if(i == states.size - 1){

      if(newBeta < beta)
        return newBeta
      else
       return beta
    }
    return minIteration(alpha, if(newBeta < beta) newBeta else beta, height, i+1, states)
  }

  @tailrec
  private def maxIteration(alpha : Int, beta : Int, height : Int, i : Int, states: Vector[InternalState]) : Int = {
    val newAlpha = min(alpha, beta, height - 1, states(i))
    if(newAlpha>= beta)
      return beta
    if(i == states.size - 1){
      if(newAlpha > alpha)
        return newAlpha
      else
        return alpha
    }
    return maxIteration(if(newAlpha > alpha) newAlpha else alpha, beta, height, i+1, states)
  }

  def min(alpha : Int, beta : Int, height : Int, state : InternalState): Int = {
    if(height == 0){
      return evaluator.evaluateState(state)
    }
    val children = state.getChildren()
    if(children.isEmpty && state.isKingAttacked(state.getOpponentColor(maximizing)))
      return INFINITY     // szach mat - gracz, nasz przeciwnik nie moze wykonac zadnego ruchu, dazymy do tego!
    if(children.isEmpty && !state.isKingAttacked(maximizing))
      return -INFINITY    // pat - chcemy go uniknac!
    val newBeta = minIteration(alpha, beta, height, 0, children)
    return newBeta
  }

  def max(alpha : Int, beta : Int, height : Int, state : InternalState): Int = {
    if(height == 0){
      return evaluator.evaluateState(state)
    }
    val children = state.getChildren()
    if(children.isEmpty)
      return -INFINITY     // szach mat lub pat - nie mozemy wykonac zadnego ruchu, chcemy tego uniknac!
    val newAlpha = maxIteration(alpha, beta, height, 0, children)
    return newAlpha
  }

  def run() : (Figure, (Int, Int)) = {
    val moves = initialState.getAllMoves()
    val x = moves.map(m => (m, min(-INFINITY, INFINITY, depth - 1, initialState.makeMove(m))))
    val e = x.maxBy(_._2)
    return e._1
  }
}