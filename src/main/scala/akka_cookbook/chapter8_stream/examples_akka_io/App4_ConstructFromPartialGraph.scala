package akka_cookbook.chapter8_stream.examples_akka_io

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, FlowShape, SourceShape}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Sink, Source, Zip}

import scala.concurrent.Future

/**
  * @author Xiangnan Ren
  */
object App4_ConstructFromPartialGraph extends App {
  implicit val actorSystem:       ActorSystem         =      ActorSystem("ConnectPartialGraph")
  implicit val actorMaterializer: ActorMaterializer   =      ActorMaterializer()

  val pairs = Source.fromGraph(GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._

    val zip = builder.add(Zip[Int, Int]())
    def ints = Source.fromIterator(() => Iterator.from(1))

    ints.filter(_ % 2 != 0) ~> zip.in0
    ints.filter(_ % 2 == 0) ~> zip.in1

    SourceShape(zip.out)
  })

  /**
    * runWith:  Connect this `Source` to a `Sink` and run it. The returned value is the materialized value
    *           of the `Sink`, e.g. the `Publisher` of a [[akka.stream.scaladsl.Sink#publisher]].
    *
    * head:     A `Sink` that materializes into a `Future` of the first value received.
    *           If the stream completes before signaling at least a single element,
    *           the Future will be failed with a [[NoSuchElementException]].
    *           If the stream signals an error errors before signaling at least a single element,
    *           the Future will be failed with the streams exception.
    *
    */
  val firstPair: Future[(Int, Int)] = pairs.runWith(Sink.head)

  val pairUpWithToString: Flow[Int, (Int, String), NotUsed] =
    Flow.fromGraph(GraphDSL.create(){ implicit builder =>
      import GraphDSL.Implicits._

      val broadcast = builder.add(Broadcast[Int](2))
      val zip = builder.add(Zip[Int, String]())

      broadcast.out(0).map(identity) ~> zip.in0
      broadcast.out(1).map(_.toString) ~> zip.in1

      FlowShape(broadcast.in, zip.out)
  })

  pairUpWithToString.runWith(Source(List(10, 20, 13, 14)), Sink.foreach(println))
}
