package game

import datatypes.Coordinate
import scala.io.Source
import org.json4s._
import org.json4s.native.JsonMethods._

case class Block(coordinates: Seq[Coordinate])
case class RawBlock(id: Int, block_dictionary: Seq[Seq[Seq[Int]]])

object Block {
  def apply(id: Int): Block = Block(id, 0)
  def apply(id: Int, orientation: Int): Block = {
    BlockDictionary.dict.get(id) match {
      case Some(x) => x.head
      case None => throw new IndexOutOfBoundsException("block id must be between 0 and 20")
    }
  }
}


object BlockDictionary {
  implicit val formats = DefaultFormats
  val dict = {
    val rawData = Source.fromResource("pieces.json").getLines.next
    parse(rawData).extract[Seq[RawBlock]].map{ rawBlock =>
       val blocks = rawBlock.block_dictionary.map{ block =>
          val coordinates = block.map(c => Coordinate(c.head, c.last))
          Block(coordinates)
       }
       rawBlock.id -> blocks
    } toMap
  }
  val filteredDict = dict.map{ case (id, blocks) =>
    (id, blocks.distinct)
  }
}
