package akka_cookbook.chapter7_remoting_and_clustering.look_up

import akka.actor.{ActorRef, ActorSystem, Props}
import akka_cookbook.chapter7_remoting_and_clustering.simple_remote_actors.SimpleActor

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._



/**
  * ActorSelection traverses the hierarchy of the actors in the path to look
  * for the desired actor. ActorSelection does not create any actors if the
  * entered path is not inhabited by a previously crated actor.
  */

// use -Dconfig.resource=application-1.conf
object LookingUpActorSelection extends App {
  val actorSystem = ActorSystem("LookingUpActors")

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

  val actorSystem = ActorSystem("LookingUpRemoteActors")
  actorSystem.actorOf(Props[SimpleActor], "remoteActor")
}