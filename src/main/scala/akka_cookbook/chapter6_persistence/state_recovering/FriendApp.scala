package akka_cookbook.chapter6_persistence.state_recovering

import akka.actor.ActorSystem
import akka.persistence.Recovery


/**
  * Add and some friend messages to create the journal and a snapshot
  */
object FriendApp extends App {
  val system = ActorSystem("test")
  val hector = system.actorOf(FriendActor.props("Hector", Recovery()))

  hector ! AddFriend(Friend("Laura"))
  hector ! AddFriend(Friend("Nancy"))
  hector ! AddFriend(Friend("Olivier"))
  hector ! AddFriend(Friend("Steve"))
  hector ! "snap"
  hector ! RemoveFriend(Friend("Nancy"))
  hector ! "print"
  Thread.sleep(2000)
  system.terminate()


}
