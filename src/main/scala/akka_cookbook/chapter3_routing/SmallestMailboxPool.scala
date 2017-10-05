package akka_cookbook.chapter3_routing

import akka.actor.{Actor, ActorSystem, Props}
import akka.routing.SmallestMailboxPool

/**
  * Created by xiangnanren on 05/10/2017.
  */
object SmallestMailbox extends App {
  val actorSystem = ActorSystem("Hello-Akka")
  val router = actorSystem.actorOf(SmallestMailboxPool(5).
    props(Props[SmallestMailboxActor]), name = "SmallestMailbox")

  for (i <- 1 to 5) {
    router ! s"Hello $i"
  }
}

class SmallestMailboxActor extends Actor {
  override def receive = {
    case msg: String => println(s" I am ${self.path.name}")
    case _ => println(s"I don't understand the message")
  }
}

