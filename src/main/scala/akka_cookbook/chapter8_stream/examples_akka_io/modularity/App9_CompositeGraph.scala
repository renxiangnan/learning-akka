package akka_cookbook.chapter8_stream.examples_akka_io.modularity

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{Balance, Broadcast, Flow, GraphDSL, Merge, RunnableGraph, Sink, Source}

/**
  * @author Xiangnan Ren
  */
object App9_CompositeGraph extends App {
  implicit val actorSystem = ActorSystem("sys")
  implicit val materializer = ActorMaterializer()


  /**
    *             --------------------------------------
    *             |                                    |
    *             --------->                           |
    *                       | C ---------------->      |
    *             --------->                    | F --->
    * A ---> B ---|                      ------->
    *             ---------> D ---> E ---|
    *                                    -------> G (output)
    *
    */


  /**
    * The graph construction in g1 implicit the port numbering feature
    */
  val g1 = RunnableGraph.fromGraph(GraphDSL.create(){ implicit builder =>
    import GraphDSL.Implicits._
    val A: Outlet[Int]                    = builder.add(Source.single(10)).out
    val B: UniformFanOutShape[Int, Int]   = builder.add(Broadcast[Int](2))
    val C: UniformFanInShape[Int, Int]    = builder.add(Merge[Int](2))
    val D: FlowShape[Int, Int]            = builder.add(Flow[Int].map(_ + 1))
    val E: UniformFanOutShape[Int, Int]   = builder.add(Balance[Int](2))
    val F: UniformFanInShape[Int, Int]    = builder.add(Merge[Int](2))
    val G: Inlet[Any]                     = builder.add(Sink.foreach(println)).in

              C    <~   F
    A ~> B ~> C    ~>   F
         B ~> D ~> E ~> F
                   E ~> G

    ClosedShape
  })
  g1.run()


  val g2 = RunnableGraph.fromGraph(GraphDSL.create(){ implicit builder =>
    import GraphDSL.Implicits._

    val B = builder.add(Broadcast[Int](2))
    val C = builder.add(Merge[Int](2))
    val E = builder.add(Balance[Int](2))
    val F = builder.add(Merge[Int](2))


    Source.single(0) ~> B.in;  B.out(0) ~> C.in(1);  C.out ~> F.in(0)
    C.in(0)  <~ F.out

    B.out(1).map(_ + 1) ~> E.in; E.out(0) ~> F.in(1)
    E.out(1) ~> Sink.foreach(println)

    ClosedShape
  })
  g2.run()




}
