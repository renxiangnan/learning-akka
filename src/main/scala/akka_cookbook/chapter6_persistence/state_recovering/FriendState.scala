package akka_cookbook.chapter6_persistence.state_recovering

sealed trait FriendEvent
case class FriendAdded(friend: Friend) extends FriendEvent
case class FriendRemoved(friend: Friend) extends FriendEvent

case class Friend(id: String)
case class AddFriend(friend: Friend)
case class RemoveFriend(friend: Friend)

case class FriendState(friends: Vector[Friend] = Vector.empty[Friend]) {
  def update(evt: FriendEvent): FriendState = evt match {
    case FriendAdded(friend) => copy(friends :+ friend)
    case FriendRemoved(friend) => copy(friends.filterNot(_ == friend))
  }
  override def toString: String = friends.mkString(",")
}