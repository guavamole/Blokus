package datatypes

import game.Block

case object NoMove

case class StartGame(gameId: Long)

case class BoardInfo(board: IndexedSeq[IndexedSeq[Int]], id: Int, blocks: Seq[Boolean]) {
  def get(coordinate: Coordinate): Option[Int] = {
    if(coordinate.outOfBounds()) None
    else Some(board(coordinate.x)(coordinate.y))
  }
}

case class Move(block: Block, coordinate: Coordinate) {
  def blockId: Int = block.id
  // Todo
  //def serialize: String = block.id.toString + coordinate.x *  * coordinate
}

// Strings that describe the AI type
case class CreateBoard(p1: String, p2: String)
