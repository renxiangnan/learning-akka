package akka_cookbook.chapter7_remoting_and_clustering.sec6_chat_app

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import akka_cookbook.chapter7_remoting_and_clustering.sec6_chat_app.ChatServer.{Connect, Disconnect, Disconnected, Message}

import scala.concurrent.duration._


/**
  * @author Xiangnan Ren
  */

class ChatClient(chatServer: ActorRef) extends Actor {
  import context.dispatcher
  implicit val timeout: Timeout = Timeout(5.seconds)

  override def preStart(): Unit = { chatServer ! Connect }

  override def receive(): Receive = {
    case Disconnect =>
      (chatServer ? Disconnect).pipeTo(self)

    case Disconnected =>
      context.stop(self)

    case body: String =>
      chatServer ! Message(self, body)

    case msg: Message =>
      println(s"Message from [${msg.authoer}] at " +
        s"[${msg.creationTimestamp}]: ${msg.body}")
  }
}

object ChatClient {
  def props(chatServer: ActorRef) = Props(new ChatClient(chatServer))
}
