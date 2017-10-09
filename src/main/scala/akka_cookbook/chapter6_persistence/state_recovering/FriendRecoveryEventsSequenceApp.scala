package akka_cookbook.chapter6_persistence.state_recovering

import akka.actor.ActorSystem
import akka.persistence.{Recovery, SnapshotSelectionCriteria}

/**
  * Place a limit up to which event is to be recovered
  */
object FriendRecoveryEventsSequenceApp extends App {
  val system = ActorSystem("test")
  val recovery = Recovery(fromSnapshot = SnapshotSelectionCriteria.None, toSequenceNr = 2L)
  val hector = system.actorOf(FriendActor.props("Hector", recovery))
  Thread.sleep(2000)
  system.terminate()

}
