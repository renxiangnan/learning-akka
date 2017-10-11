package akka_cookbook.chapter7_remoting_and_clustering.chat_app

import akka.actor.{ActorRef, ActorSystem}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

/**
  * @author Xiangnan Ren
  */

// -Dconfig.resources=application-2.conf
object ChatClientApplication1 extends App {
  val actorSystem = ActorSystem("ChatServer")
  implicit  val dispatcher: ExecutionContextExecutor = actorSystem.dispatcher
  val chatServerAddress = "akka.tcp://ChatServer@127.0.0.1:2552/user/chatServer"

  actorSystem.actorSelection(chatServerAddress).
    resolveOne(3.seconds).
    onSuccess{
      case chatServer : ActorRef =>
        val client = actorSystem.actorOf(
          ChatClient.props(chatServer), "chatClient")

        actorSystem.actorOf(
          ChatClientInterface.props(client), "chatClientInterface")
    }
}

// -Dconfig.resources=application-3.conf
object ChatClientApplication2 extends App {
  val actorSystem = ActorSystem("ChatServer")
  implicit  val dispatcher: ExecutionContextExecutor = actorSystem.dispatcher
  val chatServerAddress = "akka.tcp://ChatServer@127.0.0.1:2552/user/chatServer"

  actorSystem.actorSelection(chatServerAddress).
    resolveOne(3.seconds).
    onSuccess{
      case chatServer : ActorRef =>
        val client = actorSystem.actorOf(
          ChatClient.props(chatServer), "chatClient")

        actorSystem.actorOf(
          ChatClientInterface.props(client), "chatClientInterface")
    }
}

// -Dconfig.resource=application-1.conf
object ChatServerApplication extends App {
  val actorSystem = ActorSystem("ChatServer")
  actorSystem.actorOf(ChatServer.props, "chatServer")
}