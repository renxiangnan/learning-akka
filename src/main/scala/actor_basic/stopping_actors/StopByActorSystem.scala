package actor_basic.stopping_actors

import akka.actor._

/**
  * Created by xiangnanren on 01/10/2017.
  */



object StopByActorSystem extends App {
  val actorSystem = ActorSystem("SystemStopExample")
  val actor = actorSystem.actorOf(Props[TestActor2], name = "test")

  actor ! "hello"
  // stop our actor
  actorSystem.stop(actor)
  actorSystem.terminate()
}

class TestActor2 extends Actor {
  def receive = {
    case _ => println("a message was received")
//      context.stop(self)
  }
}