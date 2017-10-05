package akka_cookbook.chapter3_routing

import akka.actor.{Actor, ActorSystem, Props}
import akka.routing.BalancingPool

/**
  * Created by xiangnanren on 05/10/2017.
  */
object BalancingPoolApp extends App {
  val actorSystem = ActorSystem("Hello-Akka")
  val router = actorSystem.actorOf(BalancingPool(5).
    props(Props[BalancingPoolActor]))
  for (i <- 1 to 5) {
    router ! s"Hello $i"
  }

}

class BalancingPoolActor extends Actor {
  override def receive: Receive = {
    case msg: String => println(s"I am ${self.path.name}")
    case _ => println(s"I do not understand the message")
  }
}
