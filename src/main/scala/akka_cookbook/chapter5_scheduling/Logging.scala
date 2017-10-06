package akka_cookbook.chapter5_scheduling

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

/**
  * Created by xiangnanren on 05/10/2017.
  */
object Logging extends App {
  val system = ActorSystem("hello-Akka")
  val actor = system.actorOf(Props[LoggingActor], "SumActor")
  actor ! (10, 12)
  actor ! "Hello !!"
  system.terminate()

}

class LoggingActor extends Actor with ActorLogging {
  def receive: Receive = {
    case (a: Int, b: Int) => {
      log.info(s"sum of $a and $b is ${a + b}")
    }
    case msg => log.warning(s"I do not know what are you talking about : msg")
  }
}