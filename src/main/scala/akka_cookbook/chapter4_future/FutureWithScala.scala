package akka_cookbook.chapter4_future

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by xiangnanren on 05/10/2017.
  */
object FutureWithScala extends App {
  implicit val timeout = Timeout(10.seconds)

  val actorSystem = ActorSystem("Hello-Akka")
  val computationActor = actorSystem.actorOf(Props[ComputationActor])
  val future = (computationActor ? (2, 3)).mapTo[Int]
  val sum = Await.result(future, 10.seconds)
  println(s"Future Result $sum")

}

class ComputationActor extends Actor {
  override def receive: Receive = {
    case (a: Int, b: Int) => sender ! (a + b)
  }
}