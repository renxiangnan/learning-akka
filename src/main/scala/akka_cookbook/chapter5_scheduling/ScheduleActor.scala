package akka_cookbook.chapter5_scheduling

import akka.actor.{Actor, ActorSystem, Props}

import scala.concurrent.duration._


/**
  * Created by xiangnanren on 05/10/2017.
  */
object ScheduleActor extends App {
  val system = ActorSystem("Hello-Akka")
  import system.dispatcher

  val actor = system.actorOf(Props[RandomIntAdder])
  system.scheduler.scheduleOnce(2.seconds, actor, "tick")
  system.scheduler.schedule(3.seconds, 2.seconds, actor, "tick")

}


class RandomIntAdder extends Actor {
  val r = scala.util.Random
  def receive = {
    case "tick" =>
      val randomIntA = r.nextInt(10)
      val randomIntB = r.nextInt(10)

      println(s"sum of $randomIntA " +
        s"and $randomIntB is ${randomIntA + randomIntB}")
  }
}