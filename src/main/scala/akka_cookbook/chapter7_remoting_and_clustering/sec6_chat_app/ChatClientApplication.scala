package akka_cookbook.chapter7_remoting_and_clustering.sec6_chat_app

import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

/**
  * @author Xiangnan Ren
  */

// -Dconfig.resources=application-2.conf
object ChatClientApplication1 extends App {
  val config = ConfigFactory.load("application-2.conf")
  val actorSystem = ActorSystem("ChatServer", config)
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
  val config = ConfigFactory.load("application-3.conf")
  val actorSystem = ActorSystem("ChatServer", config)
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
  val config = ConfigFactory.load("application-1.conf")

  val actorSystem = ActorSystem("ChatServer", config)
  actorSystem.actorOf(ChatServer.props, "chatServer")
}