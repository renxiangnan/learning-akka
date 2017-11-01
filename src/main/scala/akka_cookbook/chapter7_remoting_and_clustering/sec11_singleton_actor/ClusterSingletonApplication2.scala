package akka_cookbook.chapter7_remoting_and_clustering.sec11_singleton_actor

import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.cluster.Cluster
import akka.cluster.singleton._
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

/**
  * @author Xiangnan Ren
  */
object ClusterSingletonApplication2 extends App {
  val config = ConfigFactory.load("singleton/application-cluster-autodown-2.conf")
  val actorSystem = ActorSystem("ClusterSystem", config)
  val cluster = Cluster(actorSystem)
  val clusterSingletonSettings = ClusterSingletonManagerSettings(actorSystem)
  val clusterSingletonManager = ClusterSingletonManager.
    props(Props[ClusterAwareSimpleActor], PoisonPill, clusterSingletonSettings)

  actorSystem.actorOf(clusterSingletonManager, "singletonClusterAwareSimpleActor")

  val singletonSimpleActor = actorSystem.actorOf(ClusterSingletonProxy.
    props(singletonManagerPath = "/user/singletonClusterAwareSimpleActor",
      settings = ClusterSingletonProxySettings(actorSystem)),
    name = "singletonSimpleActorProxy")

  import actorSystem.dispatcher

  actorSystem.scheduler.schedule(5.seconds, 5.seconds, singletonSimpleActor, "TEST")
}
