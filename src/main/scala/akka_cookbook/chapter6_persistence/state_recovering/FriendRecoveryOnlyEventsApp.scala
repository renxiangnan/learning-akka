package akka_cookbook.chapter6_persistence.state_recovering

import akka.actor.ActorSystem
import akka.persistence.{Recovery, SnapshotSelectionCriteria}


/**
  * Not recovery from snapshot and only from events
  */
object FriendRecoveryOnlyEventsApp extends App {
  val system = ActorSystem("test")
  val recovery = Recovery(fromSnapshot = SnapshotSelectionCriteria.None)
  val hector = system.actorOf(FriendActor.props("Hector", recovery))
  hector ! "print"
  Thread.sleep(2000)
  system.terminate()

}
