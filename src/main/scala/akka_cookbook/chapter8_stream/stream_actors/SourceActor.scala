package akka_cookbook.chapter8_stream.stream_actors

import akka.actor.{Actor, Props}
import akka.stream.scaladsl.SourceQueueWithComplete
import scala.concurrent.duration._

/**
  * @author Xiangnan Ren
  */
class SourceActor(sourceQueue: SourceQueueWithComplete[String]) extends Actor {
  import SourceActor._
  import context.dispatcher

  override def preStart() = {
    context.system.scheduler.schedule(0.seconds, 5.seconds, self, Tick)
  }

  override def receive: Receive = {
    case Tick => println(s"Offering element from SourceActor")
      sourceQueue.offer("Integrating!!###" +
        "Akka$$$ Actors? with }{ Akka** Streams")
  }

}

object SourceActor {
  case object Tick
  def props(sourceQueue: SourceQueueWithComplete[String]) =
    Props(new SourceActor(sourceQueue))
}