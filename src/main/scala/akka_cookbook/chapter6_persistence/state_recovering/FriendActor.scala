package akka_cookbook.chapter6_persistence.state_recovering

import akka.actor.{ActorLogging, Props}
import akka.persistence.{PersistentActor, Recovery, RecoveryCompleted, SnapshotOffer}

class FriendActor(friendId: String, r: Recovery)
  extends PersistentActor with ActorLogging {

  override val persistenceId: String = friendId
  override val recovery: Recovery = r

  var state = FriendState()

  def updateState(event: FriendEvent): Unit = {
    state = state.update(event)
  }

  override val receiveRecover: Receive = {
    case evt: FriendEvent =>
      log.info(s"Replaying event: $evt")
      updateState(evt)

    case SnapshotOffer(_, recoveredState: FriendState) =>
      log.info(s"Snapshot offered: $recoveredState")

    case RecoveryCompleted => log.info(s"Recovery completed.Current state: $state")
  }

  override val receiveCommand: Receive = {
    case AddFriend(friend) => persist(FriendAdded(friend))(updateState)
    case RemoveFriend(friend) => persist(FriendRemoved(friend))(updateState)
    case "snap" => saveSnapshot(state)
    case "print" => log.info(s"Current state: $state")
  }
}

object FriendActor {
  def props(friendId: String, recovery: Recovery) =
    Props(new FriendActor(friendId, recovery))
}