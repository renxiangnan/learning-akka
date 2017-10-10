package akka_cookbook.chapter7_remoting_and_clustering.chat_app

import akka.actor.{Actor, ActorRef, Props, Terminated}

class ChatServer extends Actor {
  import ChatServer._
  var onlineClients = Set.empty[ActorRef]

  override def receive(): Receive = {
    case Connect =>
      onlineClients += sender
      context.watch(sender)

    case Disconnect =>
      onlineClients -= sender
      // Unregisters this actor as Monitor for the provided ActorRef
      context.unwatch(sender)
      sender ! Disconnected

    case Terminated(ref) =>
      onlineClients -= ref

    case msg: Message =>
      onlineClients.filter(_ != sender).foreach(_ ! msg)
  }
}

object ChatServer {
  case object Connect
  case object Disconnect
  case object Disconnected
  case class Message(authoer: ActorRef,
                     body: String,
                     creationTimestamp: Long = System.currentTimeMillis())
  def props = Props(new ChatServer())

}
