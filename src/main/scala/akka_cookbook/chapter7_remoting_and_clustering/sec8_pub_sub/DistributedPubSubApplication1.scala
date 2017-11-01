package akka_cookbook.chapter7_remoting_and_clustering.sec8_pub_sub

import akka.actor.{ActorSystem, Props}
import akka.cluster.Cluster

import scala.concurrent.duration._
import scala.util.Random

/**
  * @author Xiangnan Ren
  */

// -Dconfig.resource=application-cluster-1.conf
object DistributedPubSubApplication1 extends App {
  val actorSystem = ActorSystem("ClusterSystem")
  val cluster = Cluster(actorSystem)

  val notificationSubscriber =actorSystem.actorOf(Props[NotificationSubscriber])
  val notificationPublisher = actorSystem.actorOf(Props[NotificationPublisher])

  val clusterAddress = cluster.selfUniqueAddress
  val notification = Notification(s"Sent from $clusterAddress", "Test!")

  import actorSystem.dispatcher

  actorSystem.scheduler.schedule(
    Random.nextInt(5).seconds,
    5.seconds,
    notificationPublisher,
    notification)

}

// -Dconfig.resource=application-cluster-2.conf
object DistributedPubSubApplication2 extends App {
  val actorSystem = ActorSystem("ClusterSystem")
  val cluster = Cluster(actorSystem)

  val notificationSubscriber =actorSystem.actorOf(Props[NotificationSubscriber])
  val notificationPublisher = actorSystem.actorOf(Props[NotificationPublisher])

  val clusterAddress = cluster.selfUniqueAddress
  val notification = Notification(s"Sent from $clusterAddress", "Test!")

  import actorSystem.dispatcher

  actorSystem.scheduler.schedule(
    Random.nextInt(5).seconds,
    5.seconds,
    notificationPublisher,
    notification)

}