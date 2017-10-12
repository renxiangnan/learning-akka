package akka_cookbook.chapter7_remoting_and_clustering.singleton_actor

import akka.actor.Actor
import akka.cluster.Cluster

/**
  * @author Xiangnan Ren
  */
class ClusterAwareSimpleActor extends Actor {
  val cluster = Cluster(context.system)
  def receive: Receive = {
    case _ => println(s"I have been created at ${cluster.selfUniqueAddress}")

  }

}
