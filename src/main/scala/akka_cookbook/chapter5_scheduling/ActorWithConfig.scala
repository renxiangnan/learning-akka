package akka_cookbook.chapter5_scheduling

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.{Config, ConfigFactory}


// Config Akka programmatically
object ActorWithConfig extends App {
  val config: Config = ConfigFactory.load("akka.conf")
  val actorSystem = ActorSystem(config.getString("myactor.actorSystem"))
  val actorName = config.getString("myactor.actorName")
  val actor = actorSystem.actorOf(Props[MyActor], actorName)
  println(actor.path)
}


class MyActor extends Actor {
  override def receive: Receive = {
    case msg: String => println(msg)
  }
}