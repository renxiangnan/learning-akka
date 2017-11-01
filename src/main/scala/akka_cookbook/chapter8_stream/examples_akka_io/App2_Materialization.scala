package akka_cookbook.chapter8_stream.examples_akka_io

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source}

/**
  * @author Xiangnan Ren
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
