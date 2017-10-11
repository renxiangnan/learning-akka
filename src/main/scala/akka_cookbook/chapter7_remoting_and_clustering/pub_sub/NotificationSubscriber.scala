package akka_cookbook.chapter7_remoting_and_clustering.pub_sub

import akka.actor.{Actor, ActorRef}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, SubscribeAck}
import akka.cluster.{Cluster, UniqueAddress}

/**
  * @author Xiangnan Ren
  */

class NotificationSubscriber extends Actor {
  val mediator: ActorRef = DistributedPubSub(context.system).mediator
  mediator ! Subscribe("notification", self)

  val cluster = Cluster(context.system)
  val clusterAddress: UniqueAddress = cluster.selfUniqueAddress

  override def receive: Receive = {
    case notification: Notification =>
      println(s"Got notification in node $clusterAddress => $notification")

    case SubscribeAck(Subscribe("notification", None, `self`)) => println("subscribing")
  }
}
