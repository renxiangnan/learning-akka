package akka_cookbook.chapter6_persistence.state_recovering

import akka.actor.ActorSystem
import akka.persistence.Recovery


/**
  * Default behavior of recovery
  */
object FriendRecoveryDefaultApp extends App {
  val system = ActorSystem("test")
  val hector = system.actorOf(FriendActor.props("Hector", Recovery()))
  hector ! "print"
  Thread.sleep(2000)
  system.terminate()

}
