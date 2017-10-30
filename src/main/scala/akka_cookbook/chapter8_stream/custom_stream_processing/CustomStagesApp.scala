package akka_cookbook.chapter8_stream.custom_stream_processing

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

/**
  * @author Xiangnan Ren
  */
object CustomStagesApp extends App {
  implicit val actorSystem: ActorSystem = ActorSystem("CustomStages")
  implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()

  val source = Source.fromGraph(new HelloAkkaStreamSource())
  val upperCaseMapper = Flow[String].map(_.toUpperCase())
  val splitter = Flow[String].mapConcat(_.split(" ").toList)
  val punctuationMapper = Flow[String].
    map(_.replaceAll("\\p{Punct}", "").replaceAll(System.lineSeparator(), ""))
  val filterEmptyElements = Flow[String].filter(_.nonEmpty)
  val wordCounterSink = Sink.fromGraph(new WordCounterSink())

  val stream = source.
    via(upperCaseMapper).
    via(splitter).
    via(punctuationMapper).
    via(filterEmptyElements).
    to(wordCounterSink)

  stream.run()
}
