package akka_cookbook.chapter7_remoting_and_clustering.chat_app

import akka.actor.{Actor, ActorRef, Props}
import akka_cookbook.chapter7_remoting_and_clustering.chat_app.ChatClientInterface.Check
import akka_cookbook.chapter7_remoting_and_clustering.chat_app.ChatServer.Disconnect

/**
  * @author Xiangnan Ren
  */

class ChatClientInterface(chatClient: ActorRef) extends Actor {
  override def preStart(): Unit = {
    println("You are logged in. Please type and press enter to send messages. " +
      "Type 'DISCONNECT' to log out.")
    self ! Check
  }

  override def receive: Receive = {
    case Check =>
      scala.io.StdIn.readLine() match {
        case "DISCONNECT" => chatClient ! Disconnect
          println("Disconnecting...")
          context.stop(self)
        case msg =>
          chatClient ! msg
          self ! Check
      }
  }
}


object ChatClientInterface {
  case object Check
  def props(chatClient: ActorRef) =
    Props(new ChatClientInterface(chatClient))
}