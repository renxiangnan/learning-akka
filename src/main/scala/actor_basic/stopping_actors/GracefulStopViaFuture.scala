package actor_basic.stopping_actors

import akka.actor._
import akka.pattern.gracefulStop

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps


/**
  * Created by xiangnanren on 01/10/2017.
  */


object GracefulStopViaFuture extends App {
  val system = ActorSystem("GracefulStopTest")
  val testActor = system.actorOf(Props[TestActor], name = "TestActor")
  // try to stop the actor gracefully
  try {

    /**
      * If the actor isn’t terminated within
      * the time‐ out, the Future results in
      * an ActorTimeoutException.
      */
    val stopped: Future[Boolean] =
      gracefulStop(testActor, 2 seconds)

    Await.result(stopped, 3 seconds)
    println("testActor was stopped")
  } catch {
    case e:Exception => e.printStackTrace()
  } finally {
    system.terminate()
  }
}

class TestActor extends Actor {
  def receive = {
    case _ => println("TestActor got message")
  }

  override def postStop { println("TestActor: postStop") }
}