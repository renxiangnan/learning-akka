package actor_basic

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

/**
  * Created by xiangnanren on 23/08/2017.
  */
object PingPongTest extends App {
  val system = ActorSystem("PingPongSystem")
  val pong = system.actorOf(Props[Pong], name = "pong")
  val ping = system.actorOf(Props(new Ping(pong)), name = "ping")

  // start the action
  ping ! StartMessage
  // commented-out so you can see all the output
//  system.shutdown
}

case object PingMessage
case object PongMessage
case object StartMessage
case object StopMessage

class Ping(pong: ActorRef) extends Actor {
  var count = 0

  def incrementAndPrint():Unit = {
    count += 1
    println("ping")
  }

  def receive = {
    case StartMessage => incrementAndPrint()
      pong ! PingMessage
    case PongMessage => incrementAndPrint()
      if (count > 99) {
      sender ! StopMessage
      println("ping stopped")
        // Terminate the actor and stop the message queue
        // cease processing messages, send a stop call to all its children
      context.stop(self)
    } else {
      sender ! PingMessage
    }
    case _ => println("Ping got something unexpected.")
  }
}

class Pong extends Actor { def receive = {
  case PingMessage => println(" pong")
    sender ! PongMessage

  case StopMessage =>
    println("pong stopped")
    context.stop(self)
  case _ => println("Pong got something unexpected.")
} }
