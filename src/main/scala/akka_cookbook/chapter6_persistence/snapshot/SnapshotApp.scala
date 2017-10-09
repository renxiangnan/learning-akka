package akka_cookbook.chapter6_persistence.snapshot

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence._
import akka_cookbook.chapter6_persistence.sample_persistence_app._

object SnapshotApp extends App {
  val system = ActorSystem("snapshot")
  val persistentActor1 = system.actorOf(Props[SnapshotActor])

  persistentActor1 ! UserUpdate("user1", Add)
  persistentActor1 ! UserUpdate("user2", Add)
  persistentActor1 ! "snap"
  Thread.sleep(2000)

  system.stop(persistentActor1)

  val persistentActor2 = system.actorOf(Props[SnapshotActor])

  Thread.sleep(2000)
  system.terminate()

}


class SnapshotActor extends PersistentActor with ActorLogging {
  override val persistenceId = "ss-id-1"
  var state = ActiveUsers()

  def updateState(event: Event): Unit = {
    state = state.update(event)
  }

  override val receiveRecover: Receive = {
    case evt: Event => updateState(evt)
    case SnapshotOffer(_, snapshot: ActiveUsers) =>
      state = snapshot
    case RecoveryCompleted => log.info(s"Recovery completed")
  }

  override val receiveCommand: Receive = {
    case UserUpdate(userId, Add) =>
      persist(AddUserEvent(userId))(updateState)

    case UserUpdate(userId, Remove) =>
      persist(RemoveUserEvent(userId))(updateState)

    case "snap" => saveSnapshot(state)

    case SaveSnapshotSuccess(metadata) =>
      log.info(s"Snapshot succes [$metadata]")

    case SaveSnapshotFailure(metadata, e) =>
      log.warning(s"Snapshot failure [$metadata], reason: [$e]")
  }

  override def postStop(): Unit = log.info("Stopping")
  override def recovery = Recovery(SnapshotSelectionCriteria.Latest)
}