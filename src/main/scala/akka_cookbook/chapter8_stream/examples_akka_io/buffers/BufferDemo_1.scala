package akka_cookbook.chapter8_stream.examples_akka_io.buffers

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Attributes}
import akka.stream.scaladsl.{Flow, Sink, Source}

/**
  * @author Xiangnan Ren
  */
object BufferDemo_1 extends App {
  implicit val actorSystem = ActorSystem("sys")
  implicit val actorMaterializer = ActorMaterializer(
    ActorMaterializerSettings(actorSystem)
    .withInputBuffer(
      initialSize = 64,
      maxSize = 64))

  /**
    *  In practice there is a cost of passing an element through the asynchronous
    *  (and therefore thread crossing) boundary which is significant.
    */
  Source(1 to 3).
    map{ i => println(s"A: $i"); i}.async.
    map{ i => println(s"B: $i"); i}.async.
    map{ i => println(s"C: $i"); i}.async.
    runWith(Sink.ignore)(actorMaterializer)

  val section = Flow[Int].map(_ * 2).async
    .addAttributes(Attributes.inputBuffer(initial = 1, max = 1)) // the buffer size of this map is 1
  val flow = section.via(Flow[Int].map(_ / 2)).async

  Source(5 to 8).via(flow).runWith(Sink.foreach(println))
}
