package game

import datatypes.Coordinate

import scala.collection.mutable.{HashMap, HashSet}
import scala.util.{Failure, Success, Try}

class Board(playerOne: Player, playerTwo: Player){
  case class Corners(player: Player, corners: HashSet[Coordinate])
  var board = HashMap[Coordinate, Player]()
  var playerCorners: Map[Player, HashSet[Coordinate]] = Map(
    playerOne -> HashSet(Coordinate(0,0)),
    playerTwo -> HashSet(Coordinate(13,13))
  )

  def set(coordinates: Coordinate, player: Player): Unit = board.put(coordinates,player)
  def get(coordinates: Coordinate): Option[Player] = board.get(coordinates)

  def placeBlock(block: Seq[Coordinate], place: Coordinate, player: Player, option: Option[Int]): Try[Board] = {
    val corners: HashSet[Coordinate] = playerCorners.get(player).get
    val coordinates: Seq[Coordinate] = block.map(c => Coordinate(c.x + place.x, c.y + place.y))
    if (validate(coordinates, player)) {
      coordinates.foreach(c => {
        set(c, player)
        if (corners.contains(c)) corners.remove(c)
      })
      coordinates.flatMap(c => Seq(
        Coordinate(c.x + 1, c.y + 1), Coordinate(c.x + 1, c.y - 1),
        Coordinate(c.x - 1, c.y + 1), Coordinate(c.x - 1, c.y - 1)
      )).foreach(c => if (!board.contains(c)) corners.add(c))
      Success(Board.this)
    } else {
      Failure(new Error("Error, invalid move"))
    }
  }

  // TODO: change block to a Block object once its done
  def validate(coordinates: Seq[Coordinate], player: Player): Boolean = {
    val corners: HashSet[Coordinate] = playerCorners.get(player).get
    val blockPlaceIsValid: (Boolean, Boolean) = coordinates.foldLeft((false,true))(
      // (foundCorner, isZero)
      (isValid: (Boolean, Boolean), c: Coordinate) => {
        // Check if its a valid corner, there should be at least one valid corner
        (isValid._1 || corners.contains(c), isValid._2 && !board.contains(c))
      }
    )
    // Check that no edges touch your own already placed pieces
    val adjacentSquaresIsValid = coordinates.flatMap(c => Seq(
      Coordinate(c.x + 1, c.y), Coordinate(c.x - 1, c.y),
      Coordinate(c.x, c.y + 1), Coordinate(c.x, c.y - 1)
    )).foldLeft(true)((isValid: Boolean, c: Coordinate) => {
      isValid && !(board.contains(c) && board.get(c).get.equals(player))
    })
    // Should all be true to be valid
    blockPlaceIsValid._1 && blockPlaceIsValid._2 && adjacentSquaresIsValid
  }

  def show(): Unit = {
    for(i <- 0 to 13; j <- 0 to 13) yield {
      val coordinate = Coordinate(i,j)
      if(board.contains(coordinate)) {
        print("|" + board.get(coordinate).get.getId() + "|")
      } else print("|_|")
      if (j.equals(13)) println("")
    }
  }
}
