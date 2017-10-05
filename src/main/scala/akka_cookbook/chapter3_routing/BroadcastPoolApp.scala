package akka_cookbook.chapter3_routing

import akka.actor.{Actor, ActorSystem, Props}
import akka.routing.BroadcastPool

/**
  * Created by xiangnanren on 05/10/2017.
  */
object BroadcastPoolApp extends App {
  val actorSystem = ActorSystem("Hello-Akka")
  val router = actorSystem.actorOf(BroadcastPool(8).
    props(Props[BroadcastPoolActor]))

  router ! "hello"

}

class BroadcastPoolActor extends Actor {
  override def receive: Receive = {
    case msg: String => println(s" $msg, I am ${self.path.name}")
    case _ => println(s" I do not understand the message")
  }
}