package akka_cookbook.chapter3_routing

import akka.actor._
import akka.pattern.ask
import akka.routing.ScatterGatherFirstCompletedPool
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by xiangnanren on 05/10/2017.
  */
object ScatterGatherFirstCompletedPoolApp extends App {
  implicit val timeout = Timeout(1.seconds)
  val actorSystem = ActorSystem("Hello-Akka")

  val router = actorSystem.actorOf(ScatterGatherFirstCompletedPool(5, within = 10.seconds).
    props(Props[ScatterGatherFirstCompletedPoolActor]))

  println(
    Await.result((router ? "hello").mapTo[String], 10.seconds)
  )
}

class ScatterGatherFirstCompletedPoolActor extends Actor {
  override def receive: Receive = {
    case msg: String => sender() ! "I say hello back to you"
    case _ => println(s" I do not understand the message")
  }
}