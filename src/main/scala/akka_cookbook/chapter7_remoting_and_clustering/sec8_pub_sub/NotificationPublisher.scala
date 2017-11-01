package akka_cookbook.chapter7_remoting_and_clustering.sec8_pub_sub

import akka.actor.{Actor, ActorRef}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish
//import akka.cl

/**
  * @author Xiangnan Ren
  */

class NotificationPublisher extends Actor {
  val mediator: ActorRef = DistributedPubSub(context.system).mediator
  override def receive: Receive = {
    case notification: Notification =>
      mediator ! Publish("notification", notification)
  }
}
