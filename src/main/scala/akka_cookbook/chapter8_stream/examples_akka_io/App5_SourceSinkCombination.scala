package akka_cookbook.chapter8_stream.examples_akka_io

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Merge, Sink, Source}
import scala.concurrent.duration._

import scala.concurrent.{Await, Future}

/**
  * @author Xiangnan Ren
  */
object App5_SourceSinkCombination extends App {
  implicit val actorSystem:       ActorSystem         =      ActorSystem("SourceSinkCombination")
  implicit val actorMaterializer: ActorMaterializer   =      ActorMaterializer()

  val sourceOne = Source(List(1))
  val sourceTwo = Source(List(2))
  val merged = Source.combine(sourceOne, sourceTwo)(Merge(_))
  val mergedResult: Future[Int] = merged.runWith(Sink.fold(0)(_+_))
  val resSource = Await.result(mergedResult, 300.millisecond)
  println(resSource)




}

