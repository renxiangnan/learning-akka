package akka_cookbook.chapter7_remoting_and_clustering.data_sharing

import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.cluster.ddata.Replicator.ReadFrom
import akka.util.Timeout
import akka_cookbook.chapter7_remoting_and_clustering.data_sharing.SubscriptionManager._
import akka.pattern.ask

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Random

/**
  * @author Xiangnan Ren
  */
object DistributedDataApplication1 extends App {
  val actorSystem = ActorSystem("ClusterSystem")
  Cluster(actorSystem).registerOnMemberUp {
    val subscriptionManager = actorSystem.actorOf(SubscriptionManager.props)
    val subscription = Subscription(
      Random.nextInt(3),
      Cluster(actorSystem).selfUniqueAddress.toString,
      System.currentTimeMillis())

    subscriptionManager ! AddSubscription(subscription)
    Thread.sleep(10000)

    implicit val timeout: Timeout = Timeout(5.seconds)
    val readMajority = ReadFrom(n = 2, timeout = 5.seconds)
    val readFrom = ReadFrom(n = 2, timeout = 5.seconds)

    Await.result(subscriptionManager ? GetSubscriptions(readMajority), 5.seconds) match {
      case GetSubscriptionsSuccess(subscriptions) =>
        println(s"The current set of subscriptions is $subscriptions")

      case GetSubscriptionsFailure =>
        println(s"Subscription manager was not able to get subscriptions successfully.")
    }

    subscriptionManager ! RemoveSubscription(subscription)
    Await.result(subscriptionManager ? GetSubscriptions(readFrom), 5.seconds) match {
      case GetSubscriptionsSuccess(subscriptions) =>
        println(s"The current set of subscriptions is $subscriptions")

      case GetSubscriptionsFailure =>
        println(s"Subscription manager was not able to get subscription successfully.")
    }
  }
}
