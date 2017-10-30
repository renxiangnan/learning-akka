package akka_cookbook.chapter8_stream.create_sources_flows_sinks

import java.nio.file.Paths
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString

/**
  * @author Xiangnan Ren
  */
object ModularizingStreamApp extends App {
  implicit val actorSystem = ActorSystem("TransformingStream")
  implicit val actorMaterializer = ActorMaterializer()

  val MaxGroups = 1000
  val path = Paths.get("src/main/resources/stream/gzipped-file.gz")
  val source = FileIO.fromPath(path)

  val gunzip = Flow[ByteString].via(Compression.gunzip())
  val utf8UppercaseMapper = Flow[ByteString].map(_.utf8String.toUpperCase)
  val utf8LowercaseMapper = Flow[ByteString].map(_.utf8String.toLowerCase)

  val splitter = Flow[String].mapConcat(_.split(" ").toList)
  val punctuationMapper = Flow[String].map(_.replaceAll("\\p{Punct}", "").
    replaceAll(System.lineSeparator(), ""))

  val filterEmptyElements = Flow[String].filter(_.nonEmpty)
  val wordCountFlow = Flow[String].groupBy(MaxGroups, identity).map(_ -> 1).
    reduce((l, r) => (l._1, l._2 + r._2)).mergeSubstreams

  val printlnSink = Sink.foreach(println)

  val streamUppercase = source.
    via(gunzip).
    via(utf8UppercaseMapper).
    via(splitter).
    via(punctuationMapper).
    via(filterEmptyElements).
    via(wordCountFlow).
    to(printlnSink)

  val streamLowercase = source.
    via(gunzip).
    via(utf8LowercaseMapper).
    via(splitter).
    via(punctuationMapper).
    via(filterEmptyElements).
    via(wordCountFlow).
    to(printlnSink)

  streamUppercase.run()
  streamLowercase.run()




}
