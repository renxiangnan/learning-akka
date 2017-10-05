package akka_cookbook.chapter3_routing

import akka.actor.{Actor, ActorSystem, PoisonPill, Props}
import akka.routing.{Broadcast, RandomPool}

/**
  * Created by xiangnanren on 05/10/2017.
  */
object SpeciallyHandledApp extends App {
  val actorSystem = ActorSystem("Hello-Akka")
  val router = actorSystem.actorOf(RandomPool(5).props(
    Props[SpeciallyHandledActor]))

  router ! Broadcast(Handle)
  router ! Broadcast(PoisonPill)
  router ! Handle
}

case object Handle

class SpeciallyHandledActor extends Actor {
  override def receive: Receive = {
    case Handle => println(s"${self.path.name} says hello")
  }
}