package actor_basic.monitoring

import akka.actor._

/**
  * Created by xiangnanren on 01/10/2017.
  */

/**
  * In the following code snippet,
  * the Parent actor creates an actor instance named kenny,
  * and then declares that it wants to “watch” kenny
  */
class Kenny extends Actor {
  //  def receive = {
  //    case _ => println("actor_basic.Kenny received a message")
  //  }


  def receive = {
    case Explode => throw new Exception("Boom!")
    case _ => println("actor_basic.Kenny received a message")
  }

  override def preStart {
    println("kenny: preStart")
  }

  override def postStop {
    println("kenny: postStop")
  }

  override def preRestart(reason: Throwable,
                          message: Option[Any]) {
    println("kenny: preRestart")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable) {
    println("kenny: postRestart")
    super.postRestart(reason)

  }
}

case object Explode

class Parent extends Actor {
  // start actor_basic.Kenny as a child, then keep an eye on it
  val kenny = context.actorOf(Props[Kenny], name = "actor_basic.Kenny")
  context.watch(kenny)

  def receive = {
    case Terminated(`kenny`) => println("OMG, they killed actor_basic.Kenny")
    case _ => println("Parent received a message")
  }
}

object DeathWatchTest extends App {
  // create the ActorSystem instance
  val system = ActorSystem("DeathWatchTest")

  // create the Parent that will create actor_basic.Kenny
  val parent = system.actorOf(Props[Parent], name = "Parent")

  // lookup kenny, then kill it
  val kenny = system.actorSelection("/user/Parent/actor_basic.Kenny")
  kenny ! PoisonPill

  Thread.sleep(1000)
  println("calling system.shutdown")
  system.terminate()
}