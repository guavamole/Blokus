package datatypes

import scala.language.implicitConversions

case class Coordinate(x: Int, y: Int) {

  def +(that: Coordinate) = new Coordinate(x + that.x, y + that.y)
  def -(that: Coordinate) = new Coordinate(x - that.x, y - that.y)

  def equals(that: Coordinate): Boolean = {
    that.x == x && that.y == y
  }
  def corners(): Seq[Coordinate] = {
    Seq(
      Coordinate(x + 1, y + 1), Coordinate(x + 1, y - 1),
      Coordinate(x - 1, y + 1), Coordinate(x - 1, y - 1)
    )
  }

  def adjacent(): Seq[Coordinate] = {
    Seq(
      Coordinate(x + 1, y), Coordinate(x - 1, y),
      Coordinate(x, y + 1), Coordinate(x , y - 1)
    )
  }

  def outOfBounds(): Boolean = {
    x < 0 || x >= Config.BOARDSIZE ||
      y < 0 || y >= Config.BOARDSIZE
  }

  def rotateRight() = Coordinate(y, -x)
  def rotateLeft() = Coordinate(-y, x)
  def flip() = Coordinate(-x, -y)

  def mirror() = Coordinate(y, x)

  override def toString(): String = " (" + x + ", " + y + ") "

}

object CoordinateUtil {

  implicit def tup2Coordinate(x: Tuple2[Int, Int]): Coordinate = new Coordinate(x._1, x._2)

  def corners(coordinates: Seq[Coordinate]): Seq[Coordinate] = {
    coordinates
      .flatMap(c => c.corners())
      .distinct
      .filterNot(c => coordinates.contains(c))
  }

  def adjacent(coordinates: Seq[Coordinate]): Seq[Coordinate] = {
    coordinates
      .flatMap(c => c.adjacent())
      .distinct
      .filterNot(c => coordinates.contains(c))
  }

  def checkInBounds(coordinates: Seq[Coordinate]) : Boolean = {
    coordinates.forall(c => !c.outOfBounds())
  }



  // Gets the top left element by first finding the smallest x then working upwards
  def topLeft(coordinates: Iterable[Coordinate]) : Coordinate = {
    val minX = coordinates.foldLeft(coordinates.head){ (a, b) =>
      if(a.x < b.x) a
      else b
    }

    coordinates.foldLeft(minX){ (a, b) =>
      if(a.x == b.x && a.y > b.y) b
      else a
    }
  }

  // Shifts the top left block to the origin
  // Returns a set because thats the most common use case
  def shiftToOrigin(coordinates: Iterable[Coordinate]): Iterable[Coordinate] = {
    val shift = topLeft(coordinates)
    coordinates.map(c => c - shift)
  }

  // Check if two blocks are the same but shifted in some way
  // Precondition: lists are distinct
  def checkShifted(c1: Seq[Coordinate], c2: Seq[Coordinate]): Boolean = {
    shiftToOrigin(c1).toSet == shiftToOrigin(c2).toSet
  }

  def getOrientations(coordinates: Seq[Coordinate]): Seq[Seq[Coordinate]] = {
    val mirror = coordinates.map(c => c.mirror())
    Seq(
      coordinates,
      coordinates.map(c => c.rotateLeft()),
      coordinates.map(c => c.rotateRight()),
      coordinates.map(c => c.flip()),
      mirror,
      mirror.map(c => c.rotateLeft()),
      mirror.map(c => c.rotateRight()),
      mirror.map(c => c.flip())
    )
  }

  val allCoordinate = for(i <- 0 until Config.BOARDSIZE; j <- 0 until Config.BOARDSIZE) yield {
    Coordinate(i, j)
  }
}