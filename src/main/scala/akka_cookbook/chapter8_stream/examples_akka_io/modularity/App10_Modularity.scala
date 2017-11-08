package akka_cookbook.chapter8_stream.examples_akka_io.modularity

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

//  val res = Source.single(19).via(partial).to(Sink.foreach(println))
//  res.run()


  val flow = Flow.fromGraph(partial)
  val source = Source.fromGraph(GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._

    val merge = builder.add(Merge[Int](2))
    Source.single(0)      ~> merge
    Source(List(2, 3, 4)) ~> merge

    SourceShape(merge.out)
  })

  val sink = {
    val nestedFlow = Flow[Int].map(_ * 2).drop(10).named("nestedFlow")
    nestedFlow
  }

  val closed = source.
    via(flow.filter(_ > 1)).to(Sink.foreach(println))
  closed.run()

}
