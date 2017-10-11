package akka_cookbook.chapter1_divingIntoAkka

import akka.actor.{Actor, ActorSystem, Props}
import akka.dispatch.ControlMessage


/**
  * Created by xiangnanren on 03/10/2017.
  */
object ControlAwareMailbox extends App {
  val actorSystem = ActorSystem("HelloAkka")
  val actor = actorSystem.actorOf(Props[Logger].withDispatcher(
    "control-aware-dispatcher"))

  actor ! "hello"
  actor ! "how are"
  actor ! "you ?"
  actor ! MyControlMessage
}


case object MyControlMessage extends ControlMessage

class Logger extends Actor {
  def receive = {
    case MyControlMessage => println("Oh, I have to " +
      "process Control message first")
    case x => println(x.toString)
  }
}