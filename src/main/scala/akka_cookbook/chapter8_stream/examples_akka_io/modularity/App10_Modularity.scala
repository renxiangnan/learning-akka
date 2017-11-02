package akka_cookbook.chapter8_stream.examples_akka_io.modularity

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{Balance, Broadcast, Flow, GraphDSL, Merge, Sink, Source}

/**
  * @author Xiangnan Ren
  */
object App10_Modularity extends App {

  implicit val actorSystem = ActorSystem("sys")
  implicit val materializer = ActorMaterializer()

  val partial = GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._
    val B: UniformFanOutShape[Int, Int]   = builder.add(Broadcast[Int](2))
    val C: UniformFanInShape[Int, Int]    = builder.add(Merge[Int](2))
    val E: UniformFanOutShape[Int, Int]   = builder.add(Balance[Int](2))
    val F: UniformFanInShape[Int, Int]    = builder.add(Merge[Int](2))


    C  <~  F
    B  ~>  C  ~>  F
    B  ~>  Flow[Int].map(_ + 1)  ~>  E  ~>  F
    FlowShape(B.in, E.out(1))
  }.named("partial")

  Source.single(0).via(partial).to(Sink.foreach(println))


}
