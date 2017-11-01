package akka_cookbook.chapter7_remoting_and_clustering.sec2_simple_remote_actors

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.{Config, ConfigFactory}


/**
  * @author Xiangnan Ren
  */

/**
  * Create 2 separate Akka applications running on different ports
  */

// Bug exist... HelloAkkaRemoting2 can not create an actor for HelloAkkaRemoting1
object HelloAkkaRemoting1 extends App {
  val conf: Config = ConfigFactory.load("application-1.conf")

  val actorSystem = ActorSystem("HelloAkkaRemoting1")
}

object HelloAkkaRemoting2 extends App {
  val conf: Config = ConfigFactory.load("application-2.conf")
  val actorSystem = ActorSystem("HelloAkkaRemoting2")
  println("Creating actor from HelloAkkaRemoting2")
  val actor = actorSystem.actorOf(
    Props[SimpleActor], "simpleRemoteActor")

  actor ! "Checking"

}
