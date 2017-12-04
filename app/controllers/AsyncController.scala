package controllers

import javax.inject._

import akka.actor.ActorSystem
import datatypes.CreateBoard
import play.api.mvc._
import players.ActorFactory

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class AsyncController @Inject()(cc: ControllerComponents, actorFactory: ActorFactory)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  def message = Action.async {
    val matchMaking = actorFactory.makeMatchMaking
    matchMaking ! CreateBoard("1", "2")
    Future.successful(Ok("ok"))
  }
}
