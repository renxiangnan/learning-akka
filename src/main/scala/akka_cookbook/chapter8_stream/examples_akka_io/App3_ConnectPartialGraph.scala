package akka_cookbook.chapter8_stream.examples_akka_io

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape, FanInShape2, UniformFanInShape}
import akka.stream.scaladsl.{GraphDSL, RunnableGraph, Sink, Source, ZipWith}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
  * @author Xiangnan Ren
  */


/**
  * Two junctions: zip1, zip2
  *
  * Graph:
  *         zip1.in0 -----------> zip1 ---(zip1.out to zip2.in0)---> zip2
  *                     |                                       |
  *         zip1.in1 ---          zip2.in1 ---------------------
  *
  */
object App3_ConnectPartialGraph {
  val pickMaxOfThree = GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._

    // ZipWith[Input1, Input2, output]
    val zip1: FanInShape2[Int, Int, Int] = builder.add(ZipWith[Int, Int, Int](math.max _))
    val zip2: FanInShape2[Int, Int, Int] = builder.add(ZipWith[Int, Int, Int](math.max _))

    zip1.out ~> zip2.in0

    UniformFanInShape(zip2.out, zip1.in0, zip1.in1, zip2.in1)
  }

  val resultSink = Sink.head[Int]

  val g = RunnableGraph.fromGraph(GraphDSL.create(resultSink) {
    implicit builder => sink =>
      import GraphDSL.Implicits._

      // Importing the partial graph will return its shape (inlets & outlets)
      val pickMax = builder.add(pickMaxOfThree)

      Source.single(10) ~> pickMax.in(0)   // zip1.in0
      Source.single(12) ~> pickMax.in(1)   // zip1.in1
      Source.single(32) ~> pickMax.in(2)   // zip2.in1
      pickMax.out ~> sink.in

      ClosedShape
  })
}

object App3MaX3_Launcher extends App {
  implicit val actorSystem:       ActorSystem         =      ActorSystem("ConnectPartialGraph")
  implicit val actorMaterializer: ActorMaterializer   =      ActorMaterializer()
  val max: Future[Int] = App3_ConnectPartialGraph.g.run
  val res = Await.result(max, 300.millisecond)

  println(res)
}