package akka_cookbook.chapter1_divingIntoAkka
import java.util.concurrent.ConcurrentLinkedQueue

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.dispatch._
import com.typesafe.config.Config
/**
  * Created by xiangnanren on 03/10/2017.
  */
object PriorityMailBoxApp extends App {
  val actorSystem = ActorSystem("HelloAkka")
  val myPriorityActor = actorSystem.actorOf(Props[MyPriorityActor].withDispatcher("prio-dispatcher"))

  myPriorityActor ! 6.0
  myPriorityActor ! 1
  myPriorityActor ! 5.0
  myPriorityActor ! 3
  myPriorityActor ! "Hello"
  myPriorityActor ! 5
  myPriorityActor ! "I am priority actor"
  myPriorityActor ! "I process string messages first, " +
    "then integer, long and others"

}


class MyPriorityActor extends Actor {
  def receive: PartialFunction[Any, Unit] = {
    case x: Int => println(x)
    case x: String => println(x)
    case x: Long => println(x)
    case x => println(x)
  }
}

class MyPriorityActorMailbox(settings: ActorSystem.Settings,
                             config: Config)
  extends UnboundedPriorityMailbox (
    // Create a priority generator
    // Smaller value means more important !
    PriorityGenerator {
      case x: String => 0
      case x: Int => 1
      case x: Long => 2
      case _ => 3
    })










