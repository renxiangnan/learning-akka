package akka_cookbook.chapter7_remoting_and_clustering.data_sharing

import akka.actor.{Actor, ActorRef, Props}
import akka.cluster.Cluster
import akka.cluster.ddata.Replicator._
import akka.cluster.ddata.{DistributedData, ORSet, ORSetKey}

/**
  * @author Xiangnan Ren
  */
class SubscriptionManager extends Actor {
  import SubscriptionManager._
  val replicator = DistributedData(context.system).replicator
  implicit val node = Cluster(context.system)

  /**
    * Implements a 'Observed Remove Set' CRDT, also called a 'OR-Set'.
    * Elements can be added and removed any number of times. Concurrent add wins
    * over remove.
   */
  private val DataKey = ORSetKey[Subscription](subscriptionKey)
  replicator ! Subscribe(DataKey, self)

  override def receive: Receive = {
    case AddSubscription(subscription) =>
      println(s"Adding: $subscription")
      replicator ! Update(DataKey, ORSet.empty[Subscription], WriteLocal)(_ + subscription)

    case RemoveSubscription(subscription) =>
      println(s"Removing $subscription")
      replicator ! Update(DataKey, ORSet.empty[Subscription], WriteLocal)(_ - subscription)

    case GetSubscriptions(consistency) =>
      replicator ! Get(DataKey, consistency, request = Some(sender()))

    case g @ GetSuccess(DataKey, Some(replyTo: ActorRef)) =>
      val value = g.get(DataKey).elements
      replicator ! GetSubscriptionsSuccess(value)

    case GetFailure(DataKey, Some(replyTo: ActorRef)) =>
      replyTo ! GetSubscriptionsFailure

    case _: UpdateResponse[_] =>       // ignore
    case c @ Changed(DataKey) =>
      val data = c.get(DataKey)
      println(s"Current elements: ${data.elements}")
  }
}


object SubscriptionManager {
  case class Subscription(id: Int, origin: String, creationTimestamp: Long)
  case class AddSubscription(subscription: Subscription)
  case class RemoveSubscription(subscription: Subscription)
  case class GetSubscriptions(consistency: ReadConsistency)

  trait GetSubscriptionResult
  case class GetSubscriptionsSuccess(subscriptions: Set[Subscription]) extends GetSubscriptionResult
  case object GetSubscriptionsFailure extends GetSubscriptionResult

  def props = Props(new SubscriptionManager())
  val subscriptionKey = "subscription_key"
}