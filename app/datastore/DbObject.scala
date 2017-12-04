package datastore

import com.google.inject.{Inject, Singleton}
import datatypes.{GameResult, Player}

import scala.concurrent.Future
import scala.util.Try

@Singleton
class DbObject @Inject()(db: SimpleDatastore) {

  def getGame(gameId: String): Future[Option[GameResult]] =
    Try(db.gameTable.get(gameId)).toFutureOption

  def writeGame(gameResult: GameResult): Future[Unit] = {
    Try(db.gameTable.put(gameResult.gameId, gameResult))
    Future.successful(())
  }

  def getPlayer(playerId: String): Future[Option[Player]] =
    Try(db.playerTable.get(playerId)).toFutureOption

  def writePlayer(player: Player): Future[Unit] = {
    Try(db.playerTable.put(player.id, player))
    Future.successful(())
  }
  private implicit class TryUtil[A](t: Try[A]) {
    def toFutureOption: Future[Option[A]] = Future.successful(t.toOption)
  }
}

