package akka_cookbook.chapter2_supervision.send_messages_to_child

import akka.actor._

/**
  * Created by xiangnanren on 05/10/2017.
  */
object SendMessagesToChild extends App {
  val actorSystem = ActorSystem("Hello-Akka")
  val parent = actorSystem.actorOf(Props[ParentActor], "parent")

  parent ! CreateChild
  parent ! CreateChild
  parent ! CreateChild
  parent ! Send

}

case class DoubleValue(x: Int)
case object CreateChild
case object Send
case class Response(x: Int)

class DoubleActor extends Actor {
  override def receive: Receive = {
    case DoubleValue(number) =>
      println(s"${self.path.name} Child got the number $number")
      sender ! Response(number * 2)
  }
}

class ParentActor extends Actor {
  val random = new scala.util.Random
  val childs = scala.collection.mutable.ListBuffer[ActorRef]()

  override def receive: Receive = {
    case CreateChild =>
      childs ++= List(context.actorOf(Props[DoubleActor]))
    case Send => println(s"Sending messages to child")
      childs.zipWithIndex foreach {
        case (child, value) => child ! DoubleValue(random.nextInt(10))
      }
    case Response(x) => println(s"Parent: Response " +
      s"from child ${sender.path.name} is $x")
  }
}
