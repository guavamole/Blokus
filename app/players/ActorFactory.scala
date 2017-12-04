package players

import akka.actor.{ActorRef, ActorSystem, Props}
import com.google.inject.{Inject, Singleton}
import game.{Board, MatchMaking}

@Singleton
class ActorFactory @Inject()(val system: ActorSystem) {
  def makePlayer(name: String): ActorRef = {
    name match {
      case "Random" => system.actorOf(Props[RandomAIPlayer])
      case "Biggest" => system.actorOf(Props[BiggestAIPlayer])
    }
  }

  def makeBoard(player1: ActorRef, player2: ActorRef): ActorRef =
    system.actorOf(Props(classOf[Board], Seq(player1, player2)))

  def makeMatchMaking: ActorRef = system.actorOf(Props(classOf[MatchMaking]))
}