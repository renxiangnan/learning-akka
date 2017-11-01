package akka_cookbook.chapter8_stream.sec6_pipelining_parallezing

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, FlowShape, UniformFanInShape, UniformFanOutShape}
import akka.stream.scaladsl.{Balance, Flow, GraphDSL, Merge, Sink, Source}

import scala.collection.immutable
import scala.util.Random

/**
  * @author Xiangnan Ren
  */
trait PipeliningParallelizing1 {
  implicit val actorSystem: ActorSystem = ActorSystem("PipeliningParallelizing")
  implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()
  val washingTasks: immutable.Seq[Clothes] = (1 to 50).map(Clothes)

  def washStage:
  Flow[Clothes, WashedClothes, NotUsed] = Flow[Clothes].map( clothes => {
    val sleepTime = Random.nextInt(3) * 100
    println(s"Washing ${clothes.id}. It will take $sleepTime milliseconds.")
    Thread.sleep(sleepTime)

    WashedClothes(clothes)
  })

  def dryStage:
  Flow[WashedClothes, DriedClothes, NotUsed] = Flow[WashedClothes].map( wc => {
    val sleepTime = Random.nextInt(3) * 100
    println(s"Drying ${wc.c.id}. It will take $sleepTime milliseconds.")
    Thread.sleep(sleepTime)

    DriedClothes(wc.c)
  })

  def parallelStage = Flow.fromGraph(
    GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._
      val dispatchLaundry: UniformFanOutShape[Clothes, Clothes] = builder.add(Balance[Clothes](3))
      val mergeLaundry: UniformFanInShape[DriedClothes, DriedClothes] = builder.add(Merge[DriedClothes](3))

      dispatchLaundry.out(0) ~> washStage.async ~> dryStage.async ~> mergeLaundry.in(0)
      dispatchLaundry.out(1) ~> washStage.async ~> dryStage.async ~> mergeLaundry.in(1)
      dispatchLaundry.out(2) ~> washStage.async ~> dryStage.async ~> mergeLaundry.in(2)


      FlowShape(dispatchLaundry.in, mergeLaundry.out)
    })

  def runGraph(testingFlow: Flow[Clothes, DriedClothes, NotUsed]) =
    Source(washingTasks).via(testingFlow).to(Sink.foreach(println)).run()

}


case class Clothes(id: Int)
case class WashedClothes(c: Clothes)
case class DriedClothes(c: Clothes)