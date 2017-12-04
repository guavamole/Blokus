package players

import akka.actor.{ActorRef, ActorSystem, Props}
import datatypes.{BoardInfo, Move}


class RandomAIPlayer extends GamePlayer {

  def makeMove(bi: BoardInfo): Option[Move] = {
    pickRandom(findAllMoves(bi))
  }
}

class BiggestAIPlayer extends GamePlayer {
  def makeMove(bi: BoardInfo): Option[Move] = {
    findAllMoves(bi).reduceOption { (a, b) =>
      if(a.block.numBlocks > b.block.numBlocks) a
      else b
    }
  }
}


