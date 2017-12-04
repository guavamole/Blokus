package players

import akka.actor.{ActorRef, ActorSystem, Props}
import com.google.inject.{Inject, Singleton}
import datastore.DbObject
import game.{Board, MatchMaking}
import play.api.Logger

@Singleton
class ActorFactory @Inject()(val system: ActorSystem, val dbObject: DbObject, val logger: Logger) {
  def makePlayer(name: String): ActorRef = {
    name match {
      case "Random" => system.actorOf(Props[RandomAIPlayer])
      case "Biggest" => system.actorOf(Props[BiggestAIPlayer])
      case _ => system.actorOf(Props[RandomAIPlayer])
    }
  }

  def makeBoard(player1: ActorRef, player2: ActorRef): ActorRef =
    system.actorOf(Props(classOf[Board], Seq(player1, player2), logger))

  def makeMatchMaking: ActorRef = system.actorOf(Props(classOf[MatchMaking], this, dbObject, logger))
}