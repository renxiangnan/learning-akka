package akka_cookbook.chapter8_stream.examples_akka_io.modularity

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

/**
  * @author Xiangnan Ren
  */
object App8_Modularity extends App {
  implicit val actorSystem = ActorSystem("sys")
  implicit val materializer = ActorMaterializer()

  /**
    * nestedSource ---> nestedSink(nestedFlow)
    */
  val nestedSource: Source[Int, NotUsed] = Source.single(0).map(_ + 1).named("nestedSource")
  val nestedFlow: Flow[Int, Int, NotUsed] = Flow[Int].filter(_ != 0).map(_ - 2).named("nestedFlow")
  val nestedSink: Sink[Int, NotUsed] = nestedFlow.to(Sink.fold(0)(_ + _)).named("nestedSink")

  val runnableGraph = nestedSource.to(nestedSink)
  val runnableGraph2 = Source.single(0).to(Sink.fold(0)(_ + _))

  val res = runnableGraph.run()
}
