package akka_cookbook.chapter8_stream.examples_akka_io.materialization

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, ClosedShape}

/**
  * @author Xiangnan Ren
  */

/**
  * When constructing flows and graphs in Akka Streams think of them as preparing a blueprint, an execution plan.
  * Stream materialization is the process of taking a stream description (the graph) and allocating all the
  * necessary resources it needs in order to run. In the case of Akka Streams this often means starting up Actors
  * which power the processing, but is not restricted to that—it could also mean opening files or socket connections etc.—
  * depending on what the stream needs.
  */
object App2_Materialization {
  val topHeadSink = Sink.head[Int]
  val bottomHeadSink = Sink.head[Int]
  val sharedDoubler = Flow[Int].map(_ * 2)

  val g = RunnableGraph.fromGraph(GraphDSL.
    create(topHeadSink, bottomHeadSink)((_, _)){
    implicit builder => (topHS, bottomHS) =>
      import GraphDSL.Implicits._

      val broadcast = builder.add(Broadcast[Int](2))
      Source.single(1) ~> broadcast.in

      broadcast.out(0) ~> sharedDoubler ~> topHS.in
      broadcast.out(1) ~> sharedDoubler ~> bottomHS.in

      ClosedShape
  })
}

object App2Launcher extends App {
  implicit val actorSystem = ActorSystem("Materialization")
  implicit val actorMaterializer = ActorMaterializer()

  App2_Materialization.g.run
}
