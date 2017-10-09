package akka_cookbook.chapter6_persistence.sample_persistence_app

import akka.persistence.{PersistentActor, SnapshotOffer}


/**
  * Each persistent actor needs to define:
  *
  *     - persistenceId:    needs to be an identifier that uniquely
  *                         identifies the actor in the whole system, including the data store
  *     - receiveRecover:   is used to replay the events or snapshot to reach the latest state
  *     - receiveCommand:   is the regular behavior of the actor, where you will persist the changes in the state
  *
  */
class SamplePersistenceActor extends PersistentActor {
  var state = ActiveUsers()
  override val persistenceId = "unique-id-1"
  override val receiveRecover: Receive = {
    case evt: Event =>
      updateState(evt)
    case SnapshotOffer(_, snapshot: ActiveUsers) =>
      state = snapshot
  }

  override def receiveCommand: Receive = {
    case UserUpdate(userId, Add) =>
      println("--- Receiving update requirement ---")
      persist(AddUserEvent(userId))(updateState)

    case UserUpdate(userId, Remove) =>
      println("--- Receiving update requirement ---")
      persist(RemoveUserEvent(userId))(updateState)

    case "snap" => println("--- Received \"snap\" command ---")
      saveSnapshot(state)

    case "print" => println("--- Received \"print\" command ---")
      println(state)
  }

  def updateState(event: Event): Unit = {
    state = state.update(event)
  }

}