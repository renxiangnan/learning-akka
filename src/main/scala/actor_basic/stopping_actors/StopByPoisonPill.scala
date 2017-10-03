package actor_basic.stopping_actors

import akka.actor._

/**
  * Created by xiangnanren on 01/10/2017.
  */


/**
  * This message will stop the actor when the message is processed.
  * The message is queued in the mailbox like an ordinary message.
  */
object StopByPoisonPill extends App {
  val system = ActorSystem("PoisonPillTest")
  val actor = system.actorOf(Props[TestActor1], name = "test")

  // the PoisonPill
  actor ! "before PoisonPill"
  actor ! PoisonPill

  /**
    *  the second String message sent to the actor
    *  won’t be re‐ ceived or processed by the actor
    *  because it will be in the mailbox after the PoisonPill.
    */
  actor ! "after PoisonPill"
  actor ! "hello?!"
  system.terminate()
}


class TestActor1 extends Actor {
  def receive = {
    case s:String => println("Message Received: " + s)
    case _ => println("TestActor got an unknown message")
  }
  override def postStop {
    println("TestActor::postStop called")
  }
}