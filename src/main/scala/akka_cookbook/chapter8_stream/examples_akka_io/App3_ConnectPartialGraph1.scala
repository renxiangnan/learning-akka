package akka_cookbook.chapter8_stream.examples_akka_io

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape, FanInShape2, UniformFanInShape}
import akka.stream.scaladsl.{GraphDSL, RunnableGraph, Sink, Source, ZipWith}
import scala.concurrent.duration._

import scala.concurrent.{Await, Future}

/**
  * @author Xiangnan Ren
  */
object App3_ConnectPartialGraph1 {
  val pickMaxOfFour = GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._

    val f_max = (x: Array[Int], y: Array[Int]) => {
      val xMax = x.max
      val yMax = y.max

      Math.max(xMax, yMax)
    }

    val zip1: FanInShape2[Array[Int], Array[Int], Int] = builder.add(ZipWith[Array[Int], Array[Int], Int](f_max))
    val zip2: FanInShape2[Array[Int], Array[Int], Int] = builder.add(ZipWith[Array[Int], Array[Int], Int](f_max))
    val zip3: FanInShape2[Int, Int, Int] = builder.add(ZipWith[Int, Int, Int](math.max))

    zip1.out ~> zip3.in0
    zip2.out ~> zip3.in1

    UniformFanInShape(zip3.out, zip1.in0, zip1.in1, zip2.in0, zip2.in1)
  }

  val resultSink = Sink.head[Int]

  val g = RunnableGraph.fromGraph(GraphDSL.create(resultSink) {
    implicit builder => sink =>
      import GraphDSL.Implicits._
      val pickMax = builder.add(pickMaxOfFour)

      Source.single(Array(12, 11)) ~> pickMax.in(0)
      Source.single(Array(52, 31)) ~> pickMax.in(1)
      Source.single(Array(23, 33)) ~> pickMax.in(2)
      Source.single(Array(56, 67)) ~> pickMax.in(3)
      pickMax.out ~> sink.in

      ClosedShape
  })
}

object App3Max4_Launcher extends App {
  implicit val actorSystem:       ActorSystem         =      ActorSystem("ConnectPartialGraph")
  implicit val actorMaterializer: ActorMaterializer   =      ActorMaterializer()

  val max: Future[Int] = App3_ConnectPartialGraph1.g.run
  val res = Await.result(max, 100.millisecond)
  println(res)
}