package akka_cookbook.chapter8_stream.sec8_stream_actors

import akka.actor.Actor
import akka_cookbook.chapter8_stream.sec8_stream_actors.SinkActor.{AckSinkActor, InitSinkActor}

/**
  * @author Xiangnan Ren
  */

class SinkActor extends Actor {
  override def receive: Receive = {
    case InitSinkActor =>
      println("SinkActor initialized")
      sender ! AckSinkActor

    case something =>
      println(s"Received [$something] in SinkActor")
      sender ! AckSinkActor
  }
}


object SinkActor {
  case object CompletedSinkActor
  case object AckSinkActor
  case object InitSinkActor
}
