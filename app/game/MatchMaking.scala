package game

import akka.actor.{Actor, ActorRef}
import com.google.inject.{Inject, Singleton}
import datastore.DbObject
import datatypes._
import play.api.Logger
import players.ActorFactory

//todo lots of work on this class

@Singleton
class MatchMaking @Inject()(factory: ActorFactory, val dbObject: DbObject, val logger: Logger) extends Actor {

  var gameCount: Long = 0
  val maxGames = 1000
  //todo: find better way to keep track of players and boards
  var boards = Seq[ActorRef]()
  var players = Seq[ActorRef]()

  var tally = (0,0,0)

  def receive = {
    case GameResult(p1, p2, score ,id) =>
      logger.debug(score.toString)
      if(gameCount < maxGames) sender() ! StartGame(gameCount)

    case CreateBoard(p1, p2)  =>
      val player1 = factory.makePlayer(p1)
      val player2 = factory.makePlayer(p2)
      val board = factory.makeBoard(player1, player2)
      players = players ++ Seq(player1, player2)
      boards = boards :+ board
      if(gameCount < maxGames) board ! StartGame(gameCount)

  }
}
