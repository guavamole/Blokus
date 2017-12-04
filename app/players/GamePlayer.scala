package players

import akka.actor._
import datatypes.Config._
import datatypes._
import game.BlockDictionary
import scala.language.postfixOps

trait GamePlayer extends Actor {

  def receive = {
    case x: BoardInfo => sender() ! {
      makeMove(x) match {
        case Some(move) => move
        case None => NoMove
      }
    }
  }

  def findAllMoves(boardInfo: BoardInfo): Seq[Move] = {

    val playerTiles = CoordinateUtil.allCoordinate.filter { coordinate =>
      boardInfo.get(coordinate).contains(boardInfo.id)
    }
    val playerAdjacent = CoordinateUtil.adjacent(playerTiles).filterNot { coordinate =>
      coordinate.outOfBounds()
    } toSet
    val playerCorner = if(checkFirstTurn(boardInfo)) {
      Set(Config.startingBlock(boardInfo.id))
    } else {
      CoordinateUtil.corners(playerTiles).filterNot { coordinate =>
        coordinate.outOfBounds()
      } toSet
    }

    def recurse(accum: Set[Coordinate], coordinate: Coordinate, depth: Int): Set[Set[Coordinate]] = {

      if (depth >= MAXTILESIZE) return Set()
      if (accum.contains(coordinate)) return Set()
      if (!boardInfo.get(coordinate).contains(EMPTYTILE)) return Set()
      if (playerAdjacent.contains(coordinate)) return Set()

      coordinate.adjacent().foldLeft(Set(accum + coordinate)) { (moves, tile) =>
        moves ++ recurse(accum + coordinate, tile, depth + 1)
      }
    }

    val allMoves = playerCorner.flatMap(coordinate => recurse(Set(), coordinate, 0)) toSeq

    allMoves.map { coordinateSet =>
      val location = CoordinateUtil.topLeft(coordinateSet.toSeq)
      val shifted = CoordinateUtil.shiftToOrigin(coordinateSet)
      val block = BlockDictionary.coordinateToBlock(shifted.toSet)
      Move(block, location)
    } filter (move => boardInfo.blocks(move.blockId))

  }

  def checkFirstTurn(boardInfo: BoardInfo): Boolean = {
    !CoordinateUtil.allCoordinate.exists(c => boardInfo.get(c).contains(boardInfo.id))
  }

  def pickRandom[A](list: Seq[A]): Option[A] = {
    if(list.isEmpty) {
      None
    } else {
      val index = math.floor(math.random() * list.length).toInt
      Some(list(index))
    }
  }

  def makeMove(bi: BoardInfo): Option[Move]
}

