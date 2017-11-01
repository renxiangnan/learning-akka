package akka_cookbook.chapter7_remoting_and_clustering.sec3_look_up

import akka.actor.{ActorRef, ActorSystem, Props}
import akka_cookbook.chapter7_remoting_and_clustering.sec2_simple_remote_actors.SimpleActor
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

/**
  * @author Xiangnan Ren
  */

/**
  * ActorSelection traverses the hierarchy of the actors in the path to look
  * for the desired actor. ActorSelection does not create any actors if the
  * entered path is not inhabited by a previously crated actor.
  */

// use -Dconfig.resource=application-1.conf
object LookingUpActorSelection extends App {
  val config = ConfigFactory.load("application-1.conf")
  val actorSystem = ActorSystem("LookingUpActors", config)

  implicit val dispatcher: ExecutionContextExecutor = actorSystem.dispatcher
  val selection = actorSystem.actorSelection(
    "akka.tcp://LookingUpRemoteActors@127.0.0.1:2553/user/remoteActor")

  selection ! "test"
  selection.resolveOne(3.seconds).onSuccess{
    case actorRef: ActorRef =>
      println("We got an ActorRef")
      actorRef ! "test"
  }
}

// -Dconfig.resource=application-2.conf
object LookingUpRemoteActors extends App {
  val config = ConfigFactory.load("application-2.conf")
  val actorSystem = ActorSystem("LookingUpRemoteActors", config)
  actorSystem.actorOf(Props[SimpleActor], "remoteActor")
}