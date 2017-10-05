package akka_cookbook.chapter2_supervision.parent_child

import akka.actor.{Actor, ActorSystem, Props}
import akka_cookbook.chapter2_supervision.send_messages_to_child

/**
  * Created by xiangnanren on 03/10/2017.
  */
object ParentChild extends App {
  val actorSystem = ActorSystem("Supervision")
  val parent = actorSystem.actorOf(Props[ParentActor], "parent")
  parent ! send_messages_to_child.CreateChild
}

case object CreateChild
case class Greet(msg: String)

class ChildActor extends Actor {
  override def receive: Receive = {
    case Greet(msg) => println(s"My parent[${self.path.parent}] " +
      s"greeted to me [${self.path}] $msg")
  }
}

class ParentActor extends Actor {
  override def receive: Receive = {
    case send_messages_to_child.CreateChild =>
      // Creates a child actor under the parent actor
      val child = context.actorOf(Props[ChildActor], "child")
      child ! Greet("Hello Child")
  }
}

