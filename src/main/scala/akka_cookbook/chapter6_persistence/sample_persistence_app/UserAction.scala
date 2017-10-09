package akka_cookbook.chapter6_persistence.sample_persistence_app

sealed trait UserAction
case object Add extends UserAction
case object Remove extends UserAction
case class UserUpdate(userId: String, action: UserAction)

sealed trait Event
case class AddUserEvent(userId: String) extends Event
case class RemoveUserEvent(userId: String) extends Event

case class ActiveUsers(users: Set[String] = Set.empty[String]) {
  def update(evt: Event): ActiveUsers = evt match {
    case AddUserEvent(userId) => copy(users + userId)
    case RemoveUserEvent(userId) => copy(users.filterNot(_ == userId))
  }

  override def toString = s"$users"
}





