package game

import datatypes.{Config, Coordinate, CoordinateUtil}
import scala.language.postfixOps
import datatypes.CoordinateUtil.tup2Coordinate

case class RawBlock(id: Int, description: String, coordinates: Seq[Coordinate])

object BlockDictionary {

  val rawBlocks = Seq(
    RawBlock(0,
      "onepiece",
      Seq((0,0))
    ),
    RawBlock(1,
      "twopiece",
      Seq((0,0), (0,1))
    ),
    RawBlock(2,
      "small corner",
      Seq((0,0), (0,1), (1,0))
    ),
    RawBlock(3,
      "three long",
      Seq((0,0), (0,1), (0,2))
    ),
    RawBlock(4,
      "small T",
      Seq((0,0), (0,1), (0,2), (1,1))
    ),
    RawBlock(5,
      "small L",
      Seq((0,0), (0,1), (0,2), (1,0))
    ),
    RawBlock(6,
      "4 Long, not legal",
      Seq((0,0), (0,1), (0,2), (0,3))
    ),
    RawBlock(7,
      "z",
      Seq((0,0), (0,1), (1,1), (1,2))
    ),
    RawBlock(8,
      "cube",
      Seq((0,0), (0,1), (1,0), (1,1))
    ),
    RawBlock(9,
      "big T",
      Seq((0,0), (0,1), (0,2), (1,1), (2,1))
    ),
    RawBlock(10,
      "star",
      Seq((0,0), (0,1), (0,2), (1,1), (1,-1))
    ),
    RawBlock(11,
      "5 long",
      Seq((0,0), (0,1), (0,2), (0,3), (0,4))
    ),
    RawBlock(12,
      "big Corner",
      Seq((0,0), (0,1), (0,2), (1,2), (2,2))
    ),
    RawBlock(13,
      "big L",
      Seq((0,0), (0,1), (0,2), (0,3), (1,3))
    ),
    RawBlock(14,
      "big Z",
      Seq((0,0), (0,1), (0,2), (1,2), (1,3))
    ),
    RawBlock(15,
      "big S",
      Seq((0,0), (1,0), (0,1), (0,2), (-1,2))
    ),
    RawBlock(16,
      "cube+",
      Seq((0,0), (1,0), (0,1), (1,1), (0,2))
    ),
    RawBlock(17,
      "space invader",
      Seq((0,0), (0,1), (0,2), (1,1), (-1,2))
    ),
    RawBlock(18,
      "extended T",
      Seq((0,0), (0,1), (0,2), (0,3), (1,1))
    ),
    RawBlock(19,
      "U",
      Seq((0,0), (0,1), (0,2), (1,0), (1, 2))
    ),
    RawBlock(20,
      "staircase",
      Seq((0,0), (0,1), (1,1), (1,2), (2,2))
    )
  )

  val dict = {
    rawBlocks.flatMap{ rawBlock =>
      CoordinateUtil
        .getOrientations(rawBlock.coordinates)
        .map(c => CoordinateUtil.shiftToOrigin(c).toSeq)
        .map { c =>
          val adjacents = CoordinateUtil.adjacent(c)
          val corners = CoordinateUtil.corners(c)
          BlockData(c, adjacents, corners)
        }
    }
  }

  // The idea is that all the blocks and their respective orientations are stored in order so
  // We know exactly where each blockdata is
  def getBlock(block: Block): BlockData = {
    val blockIndex = block.orientation + (block.id * Config.ORIENTATIONCOUNT)
    assert(blockIndex < dict.length)
    dict(blockIndex)
  }

  val reverseDict = {
    for(id <- 0 until Config.PIECECOUNT; orientation <- 0 until Config.ORIENTATIONCOUNT) yield {
      val block = Block(id, orientation)
      val data = getBlock(block)
      data.coordinates.toSet -> block
    }
  } toMap




  def coordinateToBlock(coordinates: Set[Coordinate]): Block = {
    reverseDict(coordinates)
  }
}