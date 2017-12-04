package game

import datatypes._

import scala.collection.immutable.IndexedSeq
import akka.actor._
import datatypes.{Move, NoMove, StartGame}
import play.api.Logger

import scala.collection.mutable.ListBuffer

class Board(players: Seq[ActorRef], logger: Logger) extends Actor {

  private var supervisor: ActorRef = null
  // Refreshed every round
  private var board = Array.fill(Config.BOARDSIZE, Config.BOARDSIZE)(Config.EMPTYTILE)
  private var piecesAvailable = Seq(Array.fill(Config.PIECECOUNT)(true), Array.fill(Config.PIECECOUNT)(true))
  private var currentTurn = 0
  private var isStartGame = true
  private var movesRecord = ListBuffer[Move]()
  private var gameId: Long = -1


  def receive = {
    case Move(block: Block, coordinate: Coordinate) =>
      placeBlock(block, coordinate)
      players(currentTurn) ! BoardInfo(boardState(), currentTurn, currentPlayerPieces())
    case StartGame(id) =>
      supervisor = sender()
      gameId = id
      players(currentTurn) ! BoardInfo(boardState(), currentTurn, currentPlayerPieces())
    case NoMove => supervisor ! endGame()
  }

  def set(coordinate: Coordinate): Unit =
    board(coordinate.x)(coordinate.y) = currentTurn

  def get(coordinates: Coordinate): Option[Int] = board(coordinates.x)(coordinates.y) match {
    case Config.EMPTYTILE => None
    case x if x != -1 => Some(x)
  }

  // Todo, make validation an option
  def placeBlock(block: Block, place: Coordinate): Unit = {

    val blockData = BlockDictionary.getBlock(block)
    val valid = if(Config.BOARDVALIDATION) validateMove(blockData, place) else true

    if (valid) {
      val coordinates: Seq[Coordinate] = blockData.coordinates.map(c => c + place)
      coordinates.foreach(c => set(c))
      currentTurn = (currentTurn + 1) % players.length
      piecesAvailable(currentTurn)(block.id) = false
      movesRecord += Move(block, place)
      // Turns off isStartGame when everyone made their first move
      if(isStartGame && currentTurn == 0) {
        isStartGame = false
      }
    }
  }

  def validateMove(blockData: BlockData, place: Coordinate): Boolean = {
    val coordinates: Seq[Coordinate] = blockData
      .coordinates
      .map(c => c + place)
      .filterNot(c => c.outOfBounds())

    if(isStartGame) {
      validateFirst(coordinates)
    } else {
      val adjacent: Seq[Coordinate] = blockData
        .adjacents
        .map(c => c + place)
        .filterNot(c => c.outOfBounds())

      val corners: Seq[Coordinate] = blockData
        .corners
        .map(c => c + place)
        .filterNot(c => c.outOfBounds())

      validateRegular(coordinates, adjacent, corners)
    }
  }

  def validateRegular(coordinates: Seq[Coordinate], adjacent: Seq[Coordinate], corners: Seq[Coordinate]): Boolean = {
    val inBound = CoordinateUtil.checkInBounds(coordinates)
    // One of the corners must contain a players block
    val cornerCheck = corners.exists{c: Coordinate =>
      get(c) match {
        case Some(p) => p == currentTurn
        case None => false
      }
    }
    // Check that no edges touch your own already placed pieces
    val adjacentCheck = adjacent.forall{c =>
      get(c) match {
        case Some(p) => p != currentTurn
        case None => true
      }
    }
    // Coordinates shouldnt have anything on them
    val coordinateCheck = coordinates.forall{c =>
      get(c) match {
        case Some(p) => false
        case None => true
      }
    }
    // Should all be true to be valid
    inBound && cornerCheck && adjacentCheck && coordinateCheck
  }

  // Validate the first move, I don't know a better way of doing this :/
  def validateFirst(coordinates: Seq[Coordinate]): Boolean = {
    val inBound = CoordinateUtil.checkInBounds(coordinates)

    val firstSquare = coordinates.contains(Config.startingBlock(currentTurn))

    val coordinateCheck = coordinates.forall{c =>
      get(c) match {
        case Some(p) => false
        case None => true
      }
    }

    inBound && firstSquare && coordinateCheck
  }


  // We want to pass immutables around in actor messages
  def boardState(): IndexedSeq[IndexedSeq[Int]] = board.map(row => row.toIndexedSeq).toIndexedSeq

  def currentPlayerPieces(): IndexedSeq[Boolean] = piecesAvailable(currentTurn).toIndexedSeq

  def endGame(): GameResult = {
    val p1 = CoordinateUtil.allCoordinate.count(c => get(c).contains(0))
    val p2 = CoordinateUtil.allCoordinate.count(c => get(c).contains(1))
    val moves = movesRecord
    board = Array.fill(Config.BOARDSIZE, Config.BOARDSIZE)(Config.EMPTYTILE)
    piecesAvailable = Seq(Array.fill(Config.PIECECOUNT)(true), Array.fill(Config.PIECECOUNT)(true))
    currentTurn = 0
    isStartGame = true
    movesRecord = ListBuffer[Move]()
    gameId= -1
    GameResult(p1, p2, moves)
  }

  def show(): Unit = {
    for(i <- 0 until 13; j <- 0 until 13) yield {
      val coordinate = Coordinate(i,j)
      get(coordinate) match {
        case Some(x) => print("|" + x + "|")
        case None => print("|_|")
      }
      if (j.equals(12)) println()
    }
    println()
  }
}
