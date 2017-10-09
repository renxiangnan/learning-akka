package akka_cookbook.chapter6_persistence.state_recovering

import akka.actor.ActorSystem
import akka.persistence.{Recovery, SnapshotSelectionCriteria}

object FriendRecoveryEventReplayApp extends App {
  val system =ActorSystem("test")
  val recovery = Recovery(fromSnapshot = SnapshotSelectionCriteria.None, replayMax = 3L)
  val hector = system.actorOf(FriendActor.props("Hector", recovery))

  Thread.sleep(2000)
  system.terminate()
}
