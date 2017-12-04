package game

import datatypes.{Config, Coordinate}

case class Block(id: Int, orientation: Int) {
  require(id < Config.PIECECOUNT && id >= 0)
  require(orientation < Config.ORIENTATIONCOUNT && orientation >= 0)

  def equals(that: Block): Boolean = {
    that.id == id && that.orientation == orientation
  }

  override def toString: String = {
    BlockDictionary.rawBlocks(id).description + "\n " +BlockDictionary.getBlock(this).coordinates
  }

  def blockData() = BlockDictionary.getBlock(this)

  def numBlocks = blockData().coordinates.length
}

case class BlockData(coordinates: Seq[Coordinate], adjacents: Seq[Coordinate], corners: Seq[Coordinate])


