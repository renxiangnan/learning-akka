package akka_cookbook.chapter5_scheduling

import akka.actor.{Actor, ActorSystem, Cancellable, Props}

import scala.concurrent.duration._

/**
  * Created by xiangnanren on 05/10/2017.
  */
object SchedulerWithCanceling extends App {
  val system = ActorSystem("Hello-Akka")
  import system.dispatcher

  val actor = system.actorOf(Props[CancelOperation])

  // Assign Cancellable interface type, exposes the cancel method
  val cancellable: Cancellable = system.scheduler.
    schedule(0.seconds, 2.seconds, actor, "tick")
}


class CancelOperation extends Actor {
  var i = 5
  def receive = {
    case "tick" => {
      println(s"Hi, Do you know i do the same " +
        s"task again and again, till $i times")
      i = i - 1
      if (i == 0) SchedulerWithCanceling.cancellable.cancel()
    }
  }
}
