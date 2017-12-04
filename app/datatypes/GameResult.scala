package datatypes

import java.util.UUID

case class GameResult(p1: Int, p2: Int, moves: Seq[Move], gameId: String)

object GameResult {
  def apply(p1: Int, p2: Int, moves: Seq[Move]): GameResult =
    new GameResult(p1, p2, moves, UUID.randomUUID.toString)
}

object Compress {
  def encode(games: Seq[GameResult]): String = "todo"
}
