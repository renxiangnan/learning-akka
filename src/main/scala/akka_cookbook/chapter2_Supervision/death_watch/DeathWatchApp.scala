package akka_cookbook.chapter2_supervision.death_watch

import akka.actor.{Actor, ActorSystem, Props, Terminated}

/**
  * Created by xiangnanren on 05/10/2017.
  */

/**
  * Check continuously whether a particular service is running or not
  */
object DeathWatchApp extends App {
  val actorSystem = ActorSystem("Supervision")
  val deathWatchActor = actorSystem.actorOf(Props[DeathWatchActor])
  deathWatchActor ! Service
  deathWatchActor ! Service
  Thread.sleep(1000)
  deathWatchActor ! Kill
}

case object Service
case object Kill

class ServiceActor extends Actor {
  override def receive: Receive = {
    case Service => println("I provide a special service")
  }
}

class DeathWatchActor extends Actor {
  val child = context.actorOf(Props[ServiceActor], "serviceActor")
  override def receive: Receive = {
    case Service => child ! Service
    case Kill => context.stop(child)
    case Terminated(`child`) => println("The service actor has" +
      " terminated and no longer available")
  }
}

