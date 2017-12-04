package players

import datatypes.{BoardInfo, Move}

class LearningAIPlayer extends GamePlayer{
  def makeMove(bi: BoardInfo): Option[Move] = {
    pickRandom(findAllMoves(bi))
  }

  def evaluateBoard(board: IndexedSeq[IndexedSeq[Int]]): Double = ???

  def buildMinMaxTree(boardState: BoardInfo): Move = ???
}
