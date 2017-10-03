package actor_basic.stopping_actors

import akka.actor._

/**
  * Created by xiangnanren on 01/10/2017.
  */

/**
  * With the default supervisory strategy,
  * the Kill message does what its name states,
  * terminating the target actor.
  */
class Number5 extends Actor {
  def receive = {
    case _ => println("Number5 got a message") }

  override def preStart {
    println("Number5 is alive")
  }

  override def postStop {
    println("Number5::postStop called")
  }

  override def preRestart(reason: Throwable,
                          message: Option[Any]) {
    println("Number5::preRestart called")
  }

  override def postRestart(reason: Throwable) {
    println("Number5::postRestart called")
  }
}

object KillTest extends App {
  val system = ActorSystem("KillTestSystem")
  val number5 = system.actorOf(Props[Number5], name = "Number5")

  number5 ! "hello"

  // send the Kill message
  number5 ! Kill
  system.terminate()
}




