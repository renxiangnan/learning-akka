package akka_cookbook.chapter8_stream.sec8_stream_actors

import akka.actor.{ActorSystem, Props}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Sink, Source}
import akka.util.Timeout
import akka_cookbook.chapter8_stream.sec8_stream_actors.SinkActor.{AckSinkActor, CompletedSinkActor, InitSinkActor}
import akka.pattern.ask
import scala.concurrent.duration._

/**
  * @author Xiangnan Ren
  */
object IntegratingWithActorsApplication extends App {
  implicit val actorSystem: ActorSystem = ActorSystem("IntegratingWithActors")
  implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()

  implicit val askTimeout: Timeout = Timeout(5.seconds)

  val stringCleaner = actorSystem.actorOf(Props[StringCleanerActor])
  val sinkActor = actorSystem.actorOf(Props[SinkActor])

  val source = Source.queue[String](100, OverflowStrategy.backpressure)
  val sink = Sink.actorRefWithAck[String](sinkActor,
    InitSinkActor, AckSinkActor, CompletedSinkActor)
  val queue = source.mapAsync(parallelism = 2)(elem => (stringCleaner ? elem).mapTo[String]).
    to(sink).run()

  actorSystem.actorOf(SourceActor.props(queue))
}
