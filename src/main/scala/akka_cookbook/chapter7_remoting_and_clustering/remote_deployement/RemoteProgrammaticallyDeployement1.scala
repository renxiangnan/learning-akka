package akka_cookbook.chapter7_remoting_and_clustering.remote_deployement

import akka.actor.{ActorSystem, Address, Deploy, Props}
import akka.remote.RemoteScope
import akka_cookbook.chapter7_remoting_and_clustering.simple_remote_actors.SimpleActor

object RemoteProgrammaticallyDeployement1 extends App {
  val actorSystem =
    ActorSystem("RemoteActorsProgrammatically1")
}

object RemoteProgrammaticallyDeployement2 extends App {
  val actorSystem =
    ActorSystem("RemoteActorsProgrammatically2")

  println("Creating actor from RemoteActorProgrammatically2")

  val address = Address(
    "akka.tcp",
    "RemoteActorsProgrammatically1",
    "127.0.0.1",
    2552)

  val actor = actorSystem.actorOf(Props[SimpleActor].
    withDeploy(Deploy(scope = RemoteScope(address))), "remoteActor")

  actor ! "Checking"
}


