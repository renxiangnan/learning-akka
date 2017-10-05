package akka_cookbook.chapter3_routing

import akka.actor.{Actor, ActorSystem, Props}
import akka.routing.{DefaultResizer, RoundRobinPool}

/**
  * Created by xiangnanren on 05/10/2017.
  */
object ResizablePoolApp extends App {
  val system = ActorSystem("Hello-Akka")
  val resizer = DefaultResizer(lowerBound = 10, upperBound = 10)

  val router = system.actorOf(RoundRobinPool(10000, Some(resizer)).
    props(Props[LoadActor]))

   router ! Load
}

case object Load

class LoadActor extends Actor {
  override def receive: Receive = {
    case Load => println("Handling loads of requests")
  }
}
