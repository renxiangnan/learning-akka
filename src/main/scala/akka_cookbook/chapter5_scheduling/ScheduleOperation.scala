package akka_cookbook.chapter5_scheduling

import akka.actor.ActorSystem

import scala.concurrent.duration._

/**
  * Created by xiangnanren on 05/10/2017.
  */
object ScheduleOperation extends App {
  val system = ActorSystem("Hello-Akka")
  import system.dispatcher

  // ScheduleOnce schedules the operation (function) in such a
  // way that it happens after a specified seed time but not repeatedly.
  system.scheduler.scheduleOnce(10.seconds) {
    println(s"Sum of (1 + 2) is ${1 + 2}")
  }

  system.scheduler.schedule(11.seconds, 2.seconds) {
    println(s"Hello, Sorry for disturbing you every 2 seconds")
  }
}
