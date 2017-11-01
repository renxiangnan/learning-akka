package akka_cookbook.chapter7_remoting_and_clustering.sec2_simple_remote_actors

import akka.actor.Actor

/**
  * @author Xiangnan Ren
  */

class SimpleActor extends Actor {
  override def receive: Receive = {
    case _ => println(s"I have been created at ${self.path.address.hostPort}")
  }
}

